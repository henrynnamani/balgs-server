package com.graey.Balgs.common.enums;

import lombok.Getter;

@Getter
public enum RamSize {
    GB4(4),
    GB6(6),
    GB8(8),
    GB12(12);

    private final int size;

    RamSize(int size) { this.size = size; }
}
