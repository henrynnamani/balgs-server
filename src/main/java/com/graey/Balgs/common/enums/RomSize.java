package com.graey.Balgs.common.enums;

import lombok.Getter;

@Getter
public enum RomSize {
    GB16(16),
    GB32(32),
    GB64(64),
    GB128(128),
    GB256(256);

    private final int size;

    RomSize(int size) { this.size = size; }

}
