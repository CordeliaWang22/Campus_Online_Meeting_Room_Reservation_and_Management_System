package com.example.demo.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VerificationCodeCache {

    private static final Map<String, CodeEntry> CODE_MAP = new ConcurrentHashMap<>();
    private static final long CODE_VALID_MS = 5 * 60 * 1000; // 5分钟有效
    private static final long SEND_COOLDOWN_MS = 60 * 1000; // 1分钟间隔

    public static void saveCode(String email, String code) {
        email = email.toLowerCase(); // ✅ 小写化邮箱，避免大小写导致匹配不到
        CODE_MAP.put(email, new CodeEntry(code, System.currentTimeMillis()));
        System.out.println("✅ 保存验证码：email=" + email + ", code=" + code);
    }

    public static boolean canSend(String email) {
        email = email.toLowerCase();
        CodeEntry entry = CODE_MAP.get(email);
        boolean can = entry == null || System.currentTimeMillis() - entry.sendTime >= SEND_COOLDOWN_MS;
        System.out.println("🕐 是否允许发送：email=" + email + ", canSend=" + can);
        return can;
    }

    public static boolean verify(String email, String inputCode) {
        email = email.toLowerCase();
        CodeEntry entry = CODE_MAP.get(email);

        if (entry == null) {
            System.out.println("❌ 验证失败：没有找到验证码，email=" + email);
            return false;
        }

        long now = System.currentTimeMillis();
        boolean isValidTime = now - entry.sendTime <= CODE_VALID_MS;
        boolean isCodeMatch = entry.code.equals(inputCode);

        System.out.println("🔍 验证过程：email=" + email + ", 输入=" + inputCode + ", 实际=" + entry.code + ", 是否过期=" + !isValidTime + ", 是否匹配=" + isCodeMatch);

        return isValidTime && isCodeMatch;
    }

    public static void removeCode(String email) {
        email = email.toLowerCase();
        CODE_MAP.remove(email);
        System.out.println("🧹 已移除验证码缓存：email=" + email);
    }

    private static class CodeEntry {
        String code;
        long sendTime;

        public CodeEntry(String code, long sendTime) {
            this.code = code;
            this.sendTime = sendTime;
        }
    }
}
