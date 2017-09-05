package com.randioo.majiang_collections_server.module.fight.component.score.round;

import java.util.ArrayList;
import java.util.List;

import com.randioo.mahjong_public_server.protocol.Entity.HuType;
import com.randioo.mahjong_public_server.protocol.Entity.OverMethod;

public class RoundOverResult {
    /** 座位号 */
    public int seat;
    /** 分数 */
    public int score;
    /** 胡的牌型 */
    public List<HuType> huTypeList = new ArrayList<>();
    /** 结束原因 */
    public OverMethod overMethod;
    /** 是否杠开 */
    public boolean gangKai;
    /** 明杠分数+ */
    public int mingGangScorePlus;
    /** 明杠次数+ */
    public int mingGangCountPlus;
    /** 暗杠分数+ */
    public int darkGangScorePlus;
    /** 暗杠次数+ */
    public int darkGangCountPlus;
    /** 补杠分数+ */
    public int addGangScorePlus;
    /** 补杠次数+ */
    public int addGangCountPlus;

    /** 被明杠扣分- */
    public int mingGangScoreMinus;
    /** 明杠次数- */
    public int mingGangCountMinus;
    /** 被暗杠扣分数- */
    public int darkGangScoreMinus;
    /** 暗杠次数- */
    public int darkGangCountMinus;
    /** 被补杠扣分数- */
    public int addGangScoreMinus;
    /** 补杠次数- */
    public int addGangCountMinus;

    /** 自摸+ */
    public int moScore;
    /** 抓胡+ */
    public int zhuaHuScore;
    /** 抢杠+ */
    public int qiangGangScore;
    /** 出冲- */
    public int chuChongScore;
    /** 杠冲- */
    public int gangChongScore;
    /** 扎马 */
    public int zhaMaScore;
    /** 苍蝇 */
    public int cangYingScore;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoundOverResult [seat=").append(seat).append(", score=").append(score).append(", huTypeList=")
                .append(huTypeList).append(", overMethod=").append(overMethod).append(", gangKai=").append(gangKai)
                .append(", mingGangScorePlus=").append(mingGangScorePlus).append(", mingGangCountPlus=")
                .append(mingGangCountPlus).append(", darkGangScorePlus=").append(darkGangScorePlus)
                .append(", darkGangCountPlus=").append(darkGangCountPlus).append(", addGangScorePlus=")
                .append(addGangScorePlus).append(", addGangCountPlus=").append(addGangCountPlus)
                .append(", mingGangScoreMinus=").append(mingGangScoreMinus).append(", mingGangCountMinus=")
                .append(mingGangCountMinus).append(", darkGangScoreMinus=").append(darkGangScoreMinus)
                .append(", darkGangCountMinus=").append(darkGangCountMinus).append(", addGangScoreMinus=")
                .append(addGangScoreMinus).append(", addGangCountMinus=").append(addGangCountMinus).append(", moScore=")
                .append(moScore).append(", zhuaHuScore=").append(zhuaHuScore).append(", qiangGangScore=")
                .append(qiangGangScore).append(", chuChongScore=").append(chuChongScore).append(", gangChongScore=")
                .append(gangChongScore).append(", zhaMaScore=").append(zhaMaScore).append(", cangYingScore=")
                .append(cangYingScore).append("]");
        return builder.toString();
    }

}
