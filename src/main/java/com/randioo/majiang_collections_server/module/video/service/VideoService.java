package com.randioo.majiang_collections_server.module.video.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.majiang_collections_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface VideoService extends ObserveBaseServiceInterface{

	GeneratedMessage videoGet(Role role);

	GeneratedMessage videoGetById(int id);

	GeneratedMessage videoGetByRound(int id, int round);
}
