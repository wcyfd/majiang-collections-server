/**
 * 
 */
package com.randioo.majiang_collections_server.module.fight.component.score.round.qiaoma;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.HuType;

/**
 * @Description: 敲麻胡牌类型结果
 * @author zsy
 * @date 2017年8月31日 下午1:17:49
 */
public class QiaomaHuTypeResult {
    /** 所有的番数 */
    public int fanCount;
    public int flowerCount;
    /** 勒子数 */
    public float leziCount;
    public List<HuType> typeList = new ArrayList<>();
}
