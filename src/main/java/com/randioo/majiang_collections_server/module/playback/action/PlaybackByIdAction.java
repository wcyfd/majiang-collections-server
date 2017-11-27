package com.randioo.majiang_collections_server.module.playback.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.mahjong_public_server.protocol.Playback.PlaybackGetBinaryRequest;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.majiang_collections_server.module.playback.service.PlaybackService;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(PlaybackGetBinaryRequest.class)
public class PlaybackByIdAction implements IActionSupport {

    @Autowired
    private PlaybackService playbackService;

    @Override
    public void execute(Object data, IoSession session) {
        PlaybackGetBinaryRequest response = (PlaybackGetBinaryRequest) data;
        Role role = (Role) RoleCache.getRoleBySession(session);
        int playbackId = response.getPlaybackId();
        boolean needSCStream = response.getNeedSCStream();
        playbackService.getPlaybackById(role, playbackId, needSCStream);
    }

}
