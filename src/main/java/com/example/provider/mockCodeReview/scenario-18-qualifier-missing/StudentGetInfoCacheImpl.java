package com.example.provider.service.impl;

import com.example.provider.service.StudentGetInfo;
import org.springframework.stereotype.Service;

/**
 * StudentGetInfo 接口的缓存实现（新增第二个实现类）。
 *
 * 问题 1: StudentGetInfo 接口现在有两个 @Service 实现:
 *         - StudentGetInfoImpl（原有）
 *         - StudentGetInfoCacheImpl（新增）
 *
 * 问题 2: StudentControllerDemo 中 @Autowired StudentGetInfo studentGetInfo
 *         没有使用 @Qualifier 指定具体实现，Spring 会报:
 *         "expected single matching bean but found 2"
 *
 * 问题 3: StudentNewController 中 @Autowired StudentGetInfo studentGetInfo
 *         同样没有 @Qualifier，也会注入失败。
 *
 * 需要跨文件查看所有 @Autowired StudentGetInfo 的地方:
 * - StudentControllerDemo.java:17-18
 * - StudentNewController.java:17-18
 * - StudentInfoCompositeService.java:17-18
 * - StudentDtoService.java:22-23
 * - 以及本类
 */
// 问题: 没有指定 @Qualifier("cacheImpl")，默认 bean name 为 studentGetInfoCacheImpl
// 与 StudentGetInfoImpl 的默认 bean name（studentGetInfoImpl）冲突
@Service
public class StudentGetInfoCacheImpl implements StudentGetInfo {

    // 模拟缓存
    private String cachedResult = null;

    @Override
    public String getInfo(Long id) {
        // 如果缓存中有数据则返回缓存
        if (cachedResult != null) {
            return cachedResult;
        }

        // 模拟缓存逻辑
        String result = "cached: student_" + id;
        this.cachedResult = result;
        return result;
    }

    /**
     * 清空缓存。
     */
    public void clearCache() {
        this.cachedResult = null;
    }
}
