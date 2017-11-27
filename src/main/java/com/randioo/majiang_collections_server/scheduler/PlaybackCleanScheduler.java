package com.randioo.majiang_collections_server.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.randioo.majiang_collections_server.dao.PlaybackDao;

@Component
public class PlaybackCleanScheduler {
    @Autowired
    private PlaybackDao playbackDao;

//    @Scheduled(cron = "0 40 11 * * ?")
    public void clean() {
        playbackDao.cleanPlaybackByDays(7);
    }
}
