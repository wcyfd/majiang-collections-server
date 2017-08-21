package com.randioo.majiang_collections_server.module.fight.component;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Hu;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Peng;
import com.randioo.randioo_server_base.template.Observer;

/**
 * 红中麻将规则
 * 
 * @author wcy 2017年8月21日
 *
 */

@Component
public class HongZhongMajiangRule extends MajiangRule {

    @Override
    public void update(Observer paramObserver, String paramString, Object... paramArrayOfObject) {

    }

    @Override
    public int[] getCards() {
        return null;
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
    public Hu getHu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Peng getPeng() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Chi getChi() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Gang getGang() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Class<? extends CardList>, CardList> checkOtherCardListSequence() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<Class<? extends CardList>, CardList> checkMineCardListSequence() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MajiangState execute(MajiangState majiangState) {
        switch (majiangState) {
        case STATE_CHECK_MINE_GANG_PENG_HU_CHI:
            break;
        case STATE_CHECK_OTHER_GANG_PENG_HU_CHI:
            break;
        case STATE_DISPATCH:
            break;
        case STATE_GAME_OVER:
            break;
        case STATE_GAME_READY:
            majiangState = MajiangState.STATE_GAME_START;
        case STATE_GAME_SEND_CARD:
            break;
        case STATE_GAME_START:
            break;
        case STATE_NOTICE_SEND_CARD:
            break;
        case STATE_ROUND_OVER:
            break;
        case STATE_TOUCH_CARD:
            break;
        default:
            return null;
        }
        return majiangState;
    }

    @Override
    public void onNotify(RuleableGame ruleableGame) {
        this.notifyObservers(ruleableGame.getMajiangState().toString(), ruleableGame);
    }

}
