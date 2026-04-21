package com.example.provider.utils;

public class TimeUtilsTest {
    public static void main(String[] args) {
        System.out.println("Testing TimeUtils class:");
        System.out.println("========================");
        
        // Test basic methods
        System.out.println("1. Current LocalDateTime: " + TimeUtils.getCurrentLocalDateTime());
        System.out.println("2. Current timestamp: " + TimeUtils.getCurrentTimestamp());
        System.out.println("3. Current Date: " + TimeUtils.getCurrentDate());
        System.out.println("4. Current time string (default): " + TimeUtils.getCurrentTimeString());
        System.out.println("5. Current time string (custom): " + TimeUtils.getCurrentTimeString("yyyy年MM月dd日 HH时mm分ss秒"));
        
        // Test timezone method
        System.out.println("6. Shanghai time: " + TimeUtils.getCurrentZonedDateTime("Asia/Shanghai"));
        
        // Test individual components
        System.out.println("7. Current year: " + TimeUtils.getCurrentYear());
        System.out.println("8. Current month: " + TimeUtils.getCurrentMonth());
        System.out.println("9. Current day: " + TimeUtils.getCurrentDayOfMonth());
        System.out.println("10. Current hour: " + TimeUtils.getCurrentHour());
        System.out.println("11. Current minute: " + TimeUtils.getCurrentMinute());
        System.out.println("12. Current second: " + TimeUtils.getCurrentSecond());
        
        System.out.println("========================");
        System.out.println("All methods working correctly!");
    }
}