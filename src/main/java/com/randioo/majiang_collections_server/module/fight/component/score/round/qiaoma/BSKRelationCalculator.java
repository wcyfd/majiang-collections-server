/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.entity.bo.Game;
import com.randioo.majiang_collections_server.entity.po.RoleGameInfo;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.CardList;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Chi;
import com.randioo.majiang_collections_server.module.fight.component.cardlist.Gang;

/**
 * @Description: 包三口关系
 * @author zsy
 * @date 2017年10月12日 上午11:29:30
 */
@Component
public class BSKRelationCalculator {
    /**
     * 
     * AB之间包三口 AC之间包三口
     * ABC的座位号是0，1，2 
     * 结果是：
     *  List 0         1          2
     *      list      list       list
     *       1         0          0
     *       2
     * 
     * @param game
     * @return
     */
    public List<List<Integer>> calc(Game game) {
        // 结果容器
        List<List<Integer>> list = new ArrayList<>();
        // 初始化
        for (int i = 0; i < 4; i++) {
            list.add(new ArrayList<Integer>());
        }

        if (!game.getGameConfig().getBaoSanKou()) {// 不包三口直接返回
            return list;
        }
        
        if(game.getGameConfig().getBaoSanKou()){
            return list;
        }

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            List<CardList> cardLists = info.showCardLists;
            // 一个玩家吃碰杠的座位号
            List<Integer> seatList = new ArrayList<>();
            for (CardList item : cardLists) {
                if (item instanceof Gang) {
                    Gang gang = (Gang) item;
                    // 如果是暗杠或补杠，不用统计
                    if (!(gang.dark == false && gang.peng == null)) {
                        continue;
                    }
                }
                // 出那张牌的人
                int targetSeat = item.getTargetSeat();
                seatList.add(targetSeat);
            }
            // 所有的种类
            Set<Integer> set = new HashSet<>(seatList);
            for (Integer i : set) {
                int frequency = Collections.frequency(seatList, i);
                if (frequency >= 3) {// 同一个人达到三次了
                    String gameRoleId = info.gameRoleId;
                    int seat = game.getRoleIdList().indexOf(gameRoleId);

                    List<Integer> relationList1 = list.get(seat);
                    relationList1.add(i);
                    List<Integer> relationList2 = list.get(i);
                    relationList2.add(seat);
                }
            }
        }
        return list;
    }

    // @Test
    public void dfaw() {
        Game game = new Game();

        RoleGameInfo roleGameInfo1 = new RoleGameInfo();
        RoleGameInfo roleGameInfo2 = new RoleGameInfo();
        RoleGameInfo roleGameInfo3 = new RoleGameInfo();
        RoleGameInfo roleGameInfo4 = new RoleGameInfo();

        roleGameInfo1.gameRoleId = "1";
        roleGameInfo2.gameRoleId = "2";
        roleGameInfo3.gameRoleId = "3";
        roleGameInfo4.gameRoleId = "4";

        game.getRoleIdList().add(roleGameInfo1.gameRoleId);
        game.getRoleIdList().add(roleGameInfo2.gameRoleId);
        game.getRoleIdList().add(roleGameInfo3.gameRoleId);
        game.getRoleIdList().add(roleGameInfo4.gameRoleId);

        game.getRoleIdMap().put("1", roleGameInfo1);
        game.getRoleIdMap().put("2", roleGameInfo2);
        game.getRoleIdMap().put("3", roleGameInfo3);
        game.getRoleIdMap().put("4", roleGameInfo4);

        Chi chi1 = new Chi();
        Chi chi2 = new Chi();
        Chi chi3 = new Chi();
        Chi chi4 = new Chi();
        Chi chi5 = new Chi();
        Chi chi6 = new Chi();
        Chi chi7 = new Chi();

        chi1.setTargetSeat(1);
        chi2.setTargetSeat(1);
        chi3.setTargetSeat(1);
        chi4.setTargetSeat(0);
        chi5.setTargetSeat(0);
        chi6.setTargetSeat(0);
        chi7.setTargetSeat(1);

        roleGameInfo1.showCardLists.add(chi1);
        roleGameInfo1.showCardLists.add(chi2);
        roleGameInfo1.showCardLists.add(chi3);

        roleGameInfo3.showCardLists.add(chi4);
        roleGameInfo3.showCardLists.add(chi5);
        roleGameInfo3.showCardLists.add(chi6);
        roleGameInfo3.showCardLists.add(chi7);

        List<List<Integer>> res = calc(game);
        System.out.println(res);

    }
}
