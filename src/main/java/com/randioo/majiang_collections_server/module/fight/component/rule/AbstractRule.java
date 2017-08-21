package com.randioo.majiang_collections_server.module.fight.component.rule;

import com.randioo.majiang_collections_server.entity.bo.Game;

/**
 * 抽象规则
 * 
 * @author wcy 2017年8月11日
 *
 */
public abstract class AbstractRule {
    protected abstract boolean readConfig(Game game);
}
