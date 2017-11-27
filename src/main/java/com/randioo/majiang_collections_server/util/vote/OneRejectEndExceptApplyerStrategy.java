package com.randioo.majiang_collections_server.util.vote;

import java.util.Map;
import java.util.Set;

import com.randioo.majiang_collections_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_server_base.template.Function;

/**
 * 有一个人拒绝直接判定结果,否则等待消息
 * 
 * @author wcy 2017年9月13日
 *
 */
public abstract class OneRejectEndExceptApplyerStrategy implements VoteStrategy {

    /**
     * 等待投票
     * 
     * @param joiner
     * @return
     * @author wcy 2017年7月17日
     */
    public abstract VoteResult waitVote(String joiner);

    @Override
    public boolean filterVoter(String voter, String applyer) {
        return !voter.equals(applyer);
    }

    @Override
    public VoteResult vote(String voter, boolean vote, Map<String, Boolean> voteMap, Set<String> joiners,
            Function generateFunction, String applyer) {

        // 已经投过票的人不予理睬
        if (voteMap.containsKey(voter)) {
            return VoteResult.WAIT;
        }

        voteMap.put(voter, vote);

        if (!vote) {
            generateFunction.apply();
            return VoteResult.REJECT;
        }

        WAIT_VOTE: {
            for (String joiner : joiners) {
                // 申请人就跳过
                if (joiner.equals(applyer))
                    continue;

                // 投票中没有此人就检查连接,没断就返回
                if (!voteMap.containsKey(joiner)) {
                    VoteResult voteResult = this.waitVote(joiner);
                    if (voteResult != VoteResult.WAIT) {
                        voteMap.put(joiner, voteResult == VoteResult.PASS);
                    } else {
                        break WAIT_VOTE;
                    }
                }
            }
            generateFunction.apply();
            return VoteResult.PASS;
        }
        return VoteResult.WAIT;

    }

}
