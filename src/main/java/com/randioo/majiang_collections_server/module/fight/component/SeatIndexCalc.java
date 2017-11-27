/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;

/**
 * @Description:座位索引的计算
 * @author zsy
 * @date 2017年8月11日 下午2:43:13
 */
@Component
public class SeatIndexCalc {
    /**
     * 获取当前指针的下一家
     * 
     * @param game
     * @return
     */
    public int getNext(Game game) {
        return getNext(game.getCurrentRoleIdIndex());
    }

    public int getNext(int seat) {
        int nextIndex = seat + 1;
        nextIndex = nextIndex == 4 ? 0 : nextIndex;
        return nextIndex;
    }

    public int getPre(Game game) {
        int currentIndex = game.getCurrentRoleIdIndex();
        int preIndex = currentIndex - 1;
        preIndex = preIndex == -1 ? 3 : preIndex;
        return preIndex;
    }
}
