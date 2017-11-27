/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.mahjong_public_server.protocol.Entity.GameConfigData;
import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;
import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.CallCardList;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.QiaoMaRule;
import com.randioo.majiang_collections_server.module.fight.component.RoleGameInfoGetter;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.score.round.RoundOverResult;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月1日 上午10:40:12
 */
@Component
public class QiaomaRoundOverCalculator {

    @Autowired
    private QiaomaHuTypeCalculator huTypeCalc;
    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;
    @Autowired
    private BSKRelationCalculator bskRelationCalc;

    public Map<Integer, RoundOverResult> getRoundOverResult(Game game, int flyScore, boolean checkHu) {
        List<String> roleIdList = game.getRoleIdList();
        List<CallCardList> huCallCardList = game.getHuCallCardLists();
        GameConfigData gameConfig = game.getGameConfig();
        boolean isHuangFan = game.isHuangFan();

        // 结果初始化
        Map<Integer, RoundOverResult> res = new HashMap<>();
        for (int i = 0; i < roleIdList.size(); i++) {
            RoundOverResult overResult = new RoundOverResult();
            overResult.seat = i;
            res.put(i, overResult);
        }
        // 包三口计算
        List<List<Integer>> bskList = bskRelationCalc.calc(game);
        game.logger.info("包三口结果: {}", bskList);

        for (int seat = 0; seat < roleIdList.size(); seat++) {
            RoundOverResult oneRes = res.get(seat);
            boolean containsHu = false;
            if (checkHu) {
                // 查胡
                for (CallCardList callCardList : huCallCardList) {
                    if (callCardList.masterSeat != seat)
                        continue;

                    containsHu = true;
                    RoleGameInfo roleGameInfo = roleGameInfoGetter.getRoleGameInfoBySeat(game, seat);
                    Hu hu = (Hu) callCardList.cardList;
                    // 胡的牌型
                    QiaomaHuTypeResult huTypeResult = huTypeCalc.calc(roleGameInfo, game, hu.card, hu.gangKai);
                    boolean isLezi = huTypeResult.leziCount > 0;
                    // 获取封顶分数
                    int maxScore = gameConfig.getMaxScore();
                    maxScore = maxScore == 0 ? gameConfig.getLeZiScore() : maxScore;

                    QiaoMaRule rule = (QiaoMaRule) game.getRule();
                    // 复制手牌
                    List<Integer> copyCards = new ArrayList<>(roleGameInfo.cards);
                    copyCards.add(hu.card);
                    // 花的计数 = 明花+暗花+胡牌的花数
                    int totalFlowerCount = roleGameInfo.flowerCount + rule.getDarkFlowerCount(copyCards)
                            + huTypeResult.flowerCount;
                    game.logger.info("胡牌类型：{}", huTypeResult.typeList);
                    game.logger.info("胡牌所代表的花数 :{}", huTypeResult.flowerCount);
                    game.logger.info("番数 :{}", huTypeResult.fanCount);
                    game.logger.info("明花数 :{}", roleGameInfo.flowerCount);
                    game.logger.info("暗花数 :{} ", rule.getDarkFlowerCount(copyCards));
                    game.logger.info("当前局荒番数 :{}", isHuangFan);
                    game.logger.info("苍蝇分 :{}", flyScore);
                    game.logger.info("是不是勒子算分方式 :{}", isLezi);
                    game.logger.info("勒子数: {}", huTypeResult.leziCount);
                    // 葛志宇和朱一峰说无花果不加底分
                    // 无花果算分 : 花*花的数量*牌型（番）+苍蝇分 不加底分
                    // 正常算分 :（底分+花*花的数量）*牌型（番）+ 苍蝇分
                    boolean isWuHuaGuo = huTypeResult.typeList.contains(HuType.WU_HUA_GUO);
                    // int score = isWuHuaGuo
                    // ? getWuHuaGuoScore(game, gameConfig,
                    // huTypeResult.fanCount, isHuangFan, flyScore,
                    // hu.gangChong)
                    // : getScore(game, gameConfig, huTypeResult.fanCount,
                    // isHuangFan, flyScore, totalFlowerCount,
                    // hu.gangChong);
                    // int score = getScore(game, gameConfig,
                    // huTypeResult.fanCount, isHuangFan, flyScore,
                    // totalFlowerCount, isWuHuaGuo);

                    oneRes.score = 0;
                    oneRes.huTypeList.clear();
                    oneRes.huTypeList.addAll(huTypeResult.typeList);
                    oneRes.flowerCount = totalFlowerCount;

                    if (hu.isMine) { // 自摸只能一家胡
                        int score = 0;
                        if (isLezi) {
                            score = getLeziScore(game, maxScore, flyScore, isHuangFan, false, huTypeResult.leziCount);
                            oneRes.flowerCount = 0;
                        } else {
                            score = getScore(game, gameConfig, huTypeResult.fanCount, isHuangFan, flyScore,
                                    totalFlowerCount, isWuHuaGuo, false);
                        }
                        oneRes.overMethod = OverMethod.MO_HU;

                        int winShare = bskList.get(seat).size();
                        game.logger.info("自摸时，赢家包三口份数: {}", winShare);

                        oneRes.score = score * (3 + winShare);
                        // 其他人减分
                        for (RoundOverResult roundOverResult : res.values()) {
                            if (roundOverResult.seat == seat) {
                                continue;
                            }
                            int count = 1;
                            count += bskList.get(roundOverResult.seat).size();
                            game.logger.info("自摸时，输家包三口份数: {}", count - 1);

                            roundOverResult.score -= (score * count);
                        }
                    } else {
                        RoundOverResult targetRoundOverResult = res.get(hu.getTargetSeat());
                        int score = 0;
                        if (isLezi) {
                            score = getLeziScore(game, maxScore, flyScore, isHuangFan, hu.gangChong,
                                    huTypeResult.leziCount);
                            oneRes.flowerCount = 0;
                        } else {
                            score = getScore(game, gameConfig, huTypeResult.fanCount, isHuangFan, flyScore,
                                    totalFlowerCount, isWuHuaGuo, hu.gangChong);
                        }
                        if (hu.gangChong) {
                            oneRes.overMethod = OverMethod.QIANG_GANG;
                        } else {
                            oneRes.overMethod = OverMethod.ZHUA_HU;
                        }
                        oneRes.score = score;
                        targetRoundOverResult.score -= score;
                        // 包三口的计分
                        // 一份的分数
                        int oneShareScore = score;
                        if (hu.gangChong) {// 如果抢杠
                            oneShareScore = (score - flyScore) / 3 + flyScore;
                        }
                        // 赢的份数
                        int winShare = bskList.get(seat).size();
                        game.logger.info("出铳时，赢家 包三口份数: {}", winShare);
                        oneRes.score += oneShareScore * winShare;
                        // 计算输家的包三口
                        for (Integer i : bskList.get(seat)) {
                            RoundOverResult lossRes = res.get(i);
                            lossRes.score -= oneShareScore;
                        }

                    }
                    game.logger.info("赢家最终分数: {}", oneRes.score);
                    oneRes.gangKai = hu.gangKai;
                }
            }

            // 没胡就是输，检查点冲
            if (!containsHu) {
                game.logger.info("输家家最终分数: {}", oneRes.score);
                oneRes.overMethod = OverMethod.LOSS;
                if (checkHu) {
                    // 检查是否被点冲
                    for (CallCardList item : huCallCardList) {
                        Hu hu = (Hu) item.cardList;
                        if (hu.getTargetSeat() == seat) {
                            if (hu.gangChong) {
                                oneRes.overMethod = OverMethod.GANG_CHONG;
                            } else {
                                // 点冲
                                oneRes.overMethod = OverMethod.CHU_CHONG;
                            }
                            break;
                        }
                    }
                }
            }
        }

        return res;

    }

    private int getScore(Game game, GameConfigData config, int fanCount, boolean isHuangFan, int flyScore,
            int flowerCount, boolean isWuHuaGuo, boolean isQiangGang) {
        int maxScore = config.getMaxScore();
        maxScore = maxScore == 0 ? config.getLeZiScore() : maxScore;
        int baseScore = config.getBaseScore();
        int flowerBaseScore = config.getHuaScore();
        int score = 0;

        // 无花果不加底分
        if (isWuHuaGuo) {
            // 无花果 朱一峰说的[（底花分*花的数量）*番数]≤封顶后*是否包三口*是否荒番*抢杠+苍蝇分
            game.logger.info("（底花分×10）×番数) \n: ({} × {})  × 2的{}次方  ", flowerBaseScore, flowerCount, fanCount);
            score = (flowerBaseScore * 10) * (int) Math.pow(2, fanCount);
        } else {
            // 朱一峰说的[（底分+底花分*花的数量）*番数]≤封顶后*是否包三口*是否荒番*抢杠+苍蝇分
            game.logger.info("（底分+底花分×花的数量）×番数) \n: ({} + {} × {})  × 2的{}次方  ", baseScore, flowerBaseScore,
                    flowerCount, fanCount);
            score = (baseScore + flowerBaseScore * flowerCount) * (int) Math.pow(2, fanCount);
        }
        game.logger.info("计算结果: {}", score);
        score = score > maxScore ? maxScore : score;
        game.logger.info("计算封顶后: {}", score);
        score *= (isHuangFan ? 2 : 1);
        game.logger.info("计算荒番后: {}", score);
        score *= isQiangGang ? 3 : 1;
        game.logger.info("是否抢杠：{} 计算抢杠后: {}", isQiangGang, score);
        score += flyScore;
        game.logger.info("加上苍蝇分后: {}", score);
        return score;
    }

    private int getLeziScore(Game game, int maxScore, int flyScore, boolean isHuangFan, boolean isQiangGang,
            float leZiCount) {
        // 封顶 * 勒子数 * 荒番 * 抢杠 + 苍蝇
        game.logger.info("勒子分 x 勒子数×荒番×抢杠+苍蝇");
        int score = (int) (maxScore * leZiCount * (isHuangFan ? 2 : 1) * (isQiangGang ? 3 : 1) + flyScore);
        game.logger.info("计算结果: {}", score);
        return score;
    }

    private int getWuHuaGuoScore(Game game, GameConfigData config, int fanCount, boolean isHuangFan, int flyScore) {
        int maxScore = config.getMaxScore();
        int flowerBaseScore = config.getHuaScore();
        int score = 0;
        // 封顶 ÷2 *牌型（番）+苍蝇分 不加底分
        game.logger.info(" 封顶 ÷2 ×番×荒番 \n: {} ÷ 2 × 2的{}次方  × {}  ", maxScore, fanCount, isHuangFan ? 2 : 1);
        score = (maxScore / 2) * (int) Math.pow(2, fanCount) * (isHuangFan ? 2 : 1);
        game.logger.info("计算结果: {}", score);
        score += flyScore * flowerBaseScore;
        game.logger.info("加上苍蝇分后: {}", score);
        score = score > maxScore ? maxScore : score;
        game.logger.info("计算封顶后: {}", score);
        return score;
    }
}
