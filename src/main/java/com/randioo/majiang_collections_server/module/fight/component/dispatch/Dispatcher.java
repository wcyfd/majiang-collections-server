package com.randioo.majiang_collections_server.module.fight.component.dispatch;

import java.util.List;

import com.randioo.majiang_collections_server.entity.bo.Game;

public interface Dispatcher {
    /**
     * 分牌
     * 
     * @param originCards
     * @return
     * @author wcy 2017年8月2日
     */
    public List<CardPart> dispatch(Game game, List<Integer> originCards, int partCount, int everyPartCount);
}
