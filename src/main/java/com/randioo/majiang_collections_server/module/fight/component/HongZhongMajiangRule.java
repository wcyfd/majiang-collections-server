package com.randioo.majiang_collections_server.module.fight.component;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.ZLPBaiDaHu;
import com.randioo.majiang_collections_server.protocol.Entity.GameConfigData;
import com.randioo.majiang_collections_server.protocol.Entity.GameOverMethod;
import com.randioo.randioo_server_base.template.Observer;
import com.randioo.randioo_server_base.utils.ReflectUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * 红中麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */

@Component
public class HongZhongMajiangRule extends MajiangRule {

    public final int[] cards = { 101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条
            101, 102, 103, 104, 105, 106, 107, 108, 109, // 条

            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒
            201, 202, 203, 204, 205, 206, 207, 208, 209, // 筒

            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            301, 302, 303, 304, 305, 306, 307, 308, 309, // 万
            // 401, 401, 401, 401,// 东
            // 501, 501, 501, 501,// 南
            // 601, 601, 601, 601,// 西
            // 701, 701, 701, 701,// 北
            801, 801, 801, 801,// 中
    // 901, 901, 901, 901,// 发
    // 1001, 1001, 1001, 1001,// 白
    // 1101,// 春
    // 1102,// 夏
    // 1103,// 秋
    // 1104,// 冬
    // 1105,// 梅
    // 1106,// 兰
    // 1107,// 竹
    // 1108,// 菊
    // B9,// 财神
    // BA,// 猫
    // BB,// 老鼠
    // BC,// 聚宝盆
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭

    // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
    // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
    // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
    // 11, 12, 13, 14, 15, 16, 17, 18, 19, // 条
    //
    // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
    // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
    // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
    // 21, 22, 23, 24, 25, 26, 27, 28, 29, // 筒
    //
    // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
    // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
    // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
    // 31, 32, 33, 34, 35, 36, 37, 38, 39, // 万
    // // 41, 41, 41, 41,// 东
    // // 51, 51, 51, 51,// 南
    // // 61, 61, 61, 61,// 西
    // // 71, 71, 71, 71,// 北
    // 81, 81, 81, 81,// 中
    // 91, 91, 91, 91,// 发
    // A1, A1, A1, A1,// 白
    // B1,// 梅
    // B2,// 兰
    // B3,// 竹
    // B4,// 菊
    // B5,// 春
    // B6,// 夏
    // B7,// 秋
    // B8,// 冬
    // B9,// 财神
    // BA,// 猫
    // BB,// 老鼠
    // BC,// 聚宝盆
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭
    // C1,// 白搭
    };

    @Override
    public void update(Observer paramObserver, String paramString, Object... paramArrayOfObject) {

    }

    public HongZhongMajiangRule() {
        allCardListMap.put(Gang.class, ReflectUtils.newInstance(Gang.class));
        allCardListMap.put(Peng.class, ReflectUtils.newInstance(Peng.class));
        allCardListMap.put(Chi.class, ReflectUtils.newInstance(Chi.class));
        allCardListMap.put(Hu.class, ReflectUtils.newInstance(ZLPBaiDaHu.class));

        otherCardListSequence.add(Hu.class);
        otherCardListSequence.add(Gang.class);
        otherCardListSequence.add(Peng.class);

        mineCardListSequence.add(Hu.class);
        mineCardListSequence.add(Gang.class);
    }

    @Override
    public int[] getCards() {
        return cards;
    }

    @Override
    public boolean canZhuaHu(Game game) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean canBaiDaZhuaHu(Game game) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getBaidaCard() {
        // 红中为百搭牌
        return 801;
    }

    @Override
    public void execute(RuleableGame ruleableGame, List<MajiangState> majiangStates) {
        Game game = (Game) ruleableGame;
        // switch (majiangState) {
        //
        // case STATE_CHECK_OTHER_CARDLIST:
        // break;
        // case STATE_DISPATCH:
        // majiangState = MajiangState.STATE_NOTICE_GAME_START;
        // break;
        // case STATE_GAME_OVER:
        // break;
        // case STATE_GAME_READY:
        // majiangState = MajiangState.STATE_GAME_START;
        // case STATE_GAME_SEND_CARD:
        // break;
        // case STATE_GAME_START:
        // majiangState = MajiangState.STATE_GAME_INIT;
        // break;
        // case STATE_GAME_INIT:
        // majiangState = MajiangState.STATE_CHECK_ZHUANG;
        // break;
        // case STATE_CHECK_ZHUANG:
        // majiangState = MajiangState.STATE_DISPATCH;
        // break;
        // case STATE_NOTICE_GAME_START:
        // majiangState = MajiangState.STATE_TOUCH_CARD;
        // break;
        // case STATE_SC_SEND_CARD:
        // break;
        // case STATE_ROUND_OVER:
        // if (isGameOver(game)) {
        // majiangState = MajiangState.STATE_GAME_OVER;
        // } else {
        // majiangState = MajiangState.STATE_NOTICE_READY;
        // }
        // break;
        // case STATE_SC_TOUCH_CARD:
        // majiangState = MajiangState.STATE_CHECK_MINE_CARDLIST;
        // break;
        // case STATE_TOUCH_CARD:
        // // 卡牌用完切换到游戏结束
        // if (game.getRemainCards().size() == 0) {
        // majiangState = MajiangState.STATE_ROUND_OVER;
        // } else {
        // // 通知别人我摸到牌，包括告诉自己摸到的是什么牌
        // majiangState = MajiangState.STATE_SC_TOUCH_CARD;
        // }
        // break;
        // case STATE_CHECK_MINE_CARDLIST:
        // if (game.getCallCardLists().size() > 0) {
        // majiangState = MajiangState.STATE_SC_SEND_CARDLIST_2_ROLE;
        // } else {
        // majiangState = MajiangState.STATE_SC_SEND_CARD;
        // }
        // break;
        // default:
        // return null;
        // }

        List<MajiangState> l = majiangStates;
        MajiangState s1 = majiangStates.get(0);
        switch (s1) {

        case STATE_CHECK_OTHER_CARDLIST:
            break;

        case STATE_GAME_OVER:
            break;
        case STATE_GAME_READY:
            l.set(0, MajiangState.STATE_GAME_START);
            break;
        case STATE_GAME_SEND_CARD:
            break;
        case STATE_GAME_START:
            l.set(0, MajiangState.STATE_GAME_INIT);
            break;
        case STATE_GAME_INIT:
            l.set(0, MajiangState.STATE_CHECK_ZHUANG);
            break;
        case STATE_CHECK_ZHUANG:
            l.set(0, MajiangState.STATE_DISPATCH);
            break;
        case STATE_DISPATCH:
            l.set(0, MajiangState.STATE_NOTICE_GAME_START);
            break;
        case STATE_NOTICE_GAME_START:
            l.set(0, MajiangState.STATE_TOUCH_CARD);
            break;
        case STATE_SC_SEND_CARD:
            break;
        case STATE_ROUND_OVER:
            if (isGameOver(game)) {
                l.set(0, MajiangState.STATE_GAME_OVER);
            } else {
                l.set(0, MajiangState.STATE_NOTICE_READY);
            }
            break;
        case STATE_CONSUME_MONEY:
            l.set(0, MajiangState.STATE_ROUND_OVER);
            break;
        case STATE_SC_TOUCH_CARD:
            l.set(0, MajiangState.STATE_CHECK_MINE_CARDLIST);
            break;
        case STATE_TOUCH_CARD:
            // 卡牌用完切换到游戏结束
            if (game.getRemainCards().size() == 0) {
                l.set(0, MajiangState.STATE_CONSUME_MONEY);
            } else {
                // 通知别人我摸到牌，包括告诉自己摸到的是什么牌
                l.set(0, MajiangState.STATE_SC_TOUCH_CARD);
            }
            break;
        case STATE_CHECK_MINE_CARDLIST:
            if (game.getCallCardLists().size() > 0) {
                l.set(0, MajiangState.STATE_SC_SEND_CARDLIST_2_ROLE);
            } else {
                l.set(0, MajiangState.STATE_SC_SEND_CARD);
            }
            break;
        default:
        }
    }

    private boolean isGameOver(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        GameOverMethod gameOverMethod = gameConfigData.getGameOverMethod();

        // 回合制方式游戏结束
        if (gameOverMethod == GameOverMethod.GAME_OVER_ROUND) {
            int roundCount = gameConfigData.getRoundCount();
            int finshRoundCount = game.getFinishRoundCount();

            return finshRoundCount >= roundCount;
        } else {
            String endTimeStr = gameConfigData.getEndTime();
            String nowTimeStr = TimeUtils.get_HHmmss_DateFormat().format(new Date());
            boolean isPassTime = false;
            try {
                isPassTime = TimeUtils.compareHHmmss(nowTimeStr, endTimeStr) >= 0;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return isPassTime;
        }
    }
}
