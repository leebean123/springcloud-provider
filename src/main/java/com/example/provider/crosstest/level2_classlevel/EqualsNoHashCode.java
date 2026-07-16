package com.example.provider.crosstest.level2_classlevel;

import java.util.Objects;

/**
 * Level 2: 类级别 - L2-2 重写 equals 未重写 hashCode
 *
 * 单文件方法拆分: ❌ 只看 equals 方法体不知道类缺 hashCode
 * OCR: ✅ 可看到类定义
 *
 * 问题: 重写 equals 未重写 hashCode，HashMap/HashSet 行为异常
 */
public class EqualsNoHashCode {

    private Long id;
    private String name;

    // L2-2: P1 - 重写 equals 但没重写 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EqualsNoHashCode that = (EqualsNoHashCode) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    public Long getId() { return id; }
    public String getName() { return name; }
}
