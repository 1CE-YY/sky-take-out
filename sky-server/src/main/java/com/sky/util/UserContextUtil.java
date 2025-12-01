package com.sky.util;

import com.sky.context.BaseContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户上下文工具类
 * 用于统一管理当前登录用户的ID获取，避免代码重复
 */
@Slf4j
public class UserContextUtil {

    /**
     * 获取当前登录用户ID
     *
     * @return 当前用户ID
     * @throws IllegalStateException 当用户未登录时抛出异常
     */
    public static Long getCurrentUserId() {
        Long userId = BaseContext.getCurrentId();
        if (userId == null) {
            log.error("用户未登录，无法获取用户ID");
            throw new IllegalStateException("用户未登录，请先登录");
        }
        log.debug("获取当前用户ID: {}", userId);
        return userId;
    }

    /**
     * 获取当前登录用户ID，如果未登录返回默认值
     *
     * @param defaultUserId 默认用户ID
     * @return 当前用户ID，如果未登录则返回默认值
     */
    public static Long getCurrentUserIdOrDefault(Long defaultUserId) {
        try {
            return getCurrentUserId();
        } catch (IllegalStateException e) {
            log.warn("用户未登录，使用默认用户ID: {}", defaultUserId);
            return defaultUserId;
        }
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return true-已登录，false-未登录
     */
    public static boolean isUserLoggedIn() {
        try {
            getCurrentUserId();
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * 获取当前用户ID并验证是否匹配指定用户ID
     * 用于权限验证
     *
     * @param targetUserId 目标用户ID
     * @throws SecurityException 当当前用户ID与目标用户ID不匹配时抛出异常
     */
    public static void validateUserAccess(Long targetUserId) {
        Long currentUserId = getCurrentUserId();
        if (!currentUserId.equals(targetUserId)) {
            log.error("用户权限验证失败，当前用户ID: {}, 目标用户ID: {}", currentUserId, targetUserId);
            throw new SecurityException("无权限访问该资源");
        }
        log.debug("用户权限验证通过，用户ID: {}", currentUserId);
    }

    /**
     * 设置当前用户上下文（通常在登录成功后调用）
     *
     * @param userId 用户ID
     */
    public static void setCurrentUserId(Long userId) {
        if (userId == null) {
            log.warn("尝试设置null用户ID，忽略操作");
            return;
        }
        BaseContext.setCurrentId(userId);
        log.debug("设置当前用户ID: {}", userId);
    }

    /**
     * 清除当前用户上下文（通常在登出时调用）
     */
    public static void clearCurrentUserId() {
        BaseContext.removeCurrentId();
        log.debug("清除当前用户上下文");
    }

    /**
     * 获取当前用户ID作为字符串格式
     *
     * @return 当前用户ID的字符串表示
     */
    public static String getCurrentUserIdAsString() {
        return String.valueOf(getCurrentUserId());
    }

    /**
     * 在方法中执行用户相关的操作，自动处理用户ID获取和异常处理
     *
     * @param operation 要执行的操作，接收用户ID参数
     * @param <T>       返回值类型
     * @return 操作结果
     * @throws IllegalStateException 当用户未登录时抛出异常
     */
    public static <T> T executeWithUserId(UserOperation<T> operation) {
        Long userId = getCurrentUserId();
        return operation.execute(userId);
    }

    /**
     * 用户操作函数式接口
     *
     * @param <T> 返回值类型
     */
    @FunctionalInterface
    public interface UserOperation<T> {
        T execute(Long userId);
    }
}