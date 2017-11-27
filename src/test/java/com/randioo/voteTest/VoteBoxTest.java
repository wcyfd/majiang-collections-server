package com.randioo.voteTest;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.util.vote.OneRejectEndExceptApplyerStrategy;
import com.randioo.majiang_collections_server.util.vote.VoteBox;
import com.randioo.majiang_collections_server.util.vote.VoteBox.VoteResult;

public class VoteBoxTest {
    @Test
    public void test() {
        final VoteBox voteBox = new VoteBox();
        voteBox.setStrategy(new OneRejectEndExceptApplyerStrategy() {

            @Override
            public VoteResult waitVote(String joiner) {
                return VoteResult.WAIT;
            }
        });

        voteBox.getJoinVoteSet().addAll(Arrays.asList("1", "2", "3", "4"));
        final int voteId = voteBox.applyVote("1");
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                synchronized (voteBox) {

                    System.out.println("2拒绝");
                    voteBox.vote("2", false, voteId);
                }

                System.out.println(voteBox.getVoteId());
            }
        });
        Thread t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                synchronized (voteBox) {
                    System.out.println("3同意");
                    voteBox.vote("3", true, voteId);
                }
                System.out.println(voteBox.getVoteId());
            }
        });
        // new Thread(new Runnable() {
        //
        // @Override
        // public void run() {
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // synchronized (voteBox) {
        // voteBox.vote("4", true, voteId);
        // }
        // System.out.println(voteBox.getVoteId());
        // }
        // }).start();

        Thread t3 = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    System.out.println(voteBox.getVoteId());
                    System.out.println(voteBox.getResult());
                }
            }
        });
        t1.start();
        t2.start();
        t3.start();
        System.out.println(voteBox.getResult());
        System.out.println(voteBox.getVoteId());

    }

    @Test
    public void set() {
        Set<Role> set = new HashSet<>();
        Role role = new Role();
        set.add(role);
        System.out.println(set.size());
        role.setAccount("w");
        set.remove(role);
        System.out.println(set.size());
    }
}
