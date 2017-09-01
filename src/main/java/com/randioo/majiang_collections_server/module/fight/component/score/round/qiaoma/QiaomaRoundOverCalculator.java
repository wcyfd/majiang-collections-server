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
    private QiaomaHuTypeCalculator huTyoeCalc;
    @Autowired
    private RoleGameInfoGetter roleGameInfoGetter;

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
                    QiaomaHuTypeResult huTypeResult = huTyoeCalc.calc(roleGameInfo, game, hu.card, hu.gangKai);
                    QiaoMaRule rule = (QiaoMaRule) game.getRule();
                    // 复制手牌
                    List<Integer> copyCards = new ArrayList<>(roleGameInfo.cards);
                    copyCards.add(hu.card);
                    // 花的计数 = 无花果的花+明花+暗花
                    int totalFlowerCount = huTypeResult.flowerCount + roleGameInfo.flowerCount
                            + rule.getDarkFlowerCount(copyCards);

                    int score = getScore(gameConfig, huTypeResult.fanCount, isHuangFan, flyScore, totalFlowerCount);

                    if (hu.isMine) { // 自摸只能一家胡
                        oneRes.overMethod = OverMethod.MO_HU;
                        oneRes.huTypeList = huTypeResult.typeList;
                        oneRes.score = score * 3;
                        // 其他人减分
                        for (RoundOverResult roundOverResult : res.values()) {
                            if (roundOverResult.seat == seat) {
                                continue;
                            }
                            roundOverResult.score += -score;
                        }
                    } else {
                        if (hu.gangChong) {
                            oneRes.overMethod = OverMethod.QIANG_GANG;
                        } else {
                            oneRes.overMethod = OverMethod.ZHUA_HU;
                        }
                        oneRes.score = score;
                        RoundOverResult targetRoundOverResult = res.get(hu.getTargetSeat());
                        targetRoundOverResult.score += -score;
                    }
                    oneRes.gangKai = hu.gangKai;
                }
            }

            // 没胡就是输，检查点冲
            if (!containsHu) {
                oneRes.overMethod = OverMethod.LOSS;
                if (checkHu) {
                    // 检查是否被点冲
                    for (CallCardList item : huCallCardList) {
                        Hu hu = (Hu) item.cardList;
                        if (hu.getTargetSeat() == seat) {
                            // 点冲
                            oneRes.overMethod = OverMethod.CHU_CHONG;
                            break;
                        }
                    }
                }
            }
        }
        return res;
    }

    private int getScore(GameConfigData config, int fanCount, boolean isHuangFan, int flyScore, int flowerCount) {
        int maxScore = config.getMaxScore();
        int baseScore = config.getBaseScore();
        int flowerBaseScore = config.getHuaScore();
        int score = 0;
        score = (baseScore + flowerBaseScore * flowerCount) * (int) Math.pow(2, fanCount) * (isHuangFan ? 2 : 1);
        score += flyScore;
        score = score > maxScore ? maxScore : score;
        return score;
    }

}
