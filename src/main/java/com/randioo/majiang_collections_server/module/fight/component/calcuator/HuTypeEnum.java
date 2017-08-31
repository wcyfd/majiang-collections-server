package com.randioo.majiang_collections_server.module.fight.component.calcuator;

public enum HuTypeEnum {
    SI_BAI_DA(2), WU_BAI_DA(1), PAO_DAI_DA(1), GANG_KAI(1), MEN_QING(1), DA_DIAO_CHE(1);

    public int fan;

    HuTypeEnum(int fan) {
        this.fan = fan;
    }
}
