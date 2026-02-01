package com.lyric.lyric.Utils.text;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("PlaceholderUtils 测试类")
class PlaceholderUtilsTest {

    @Test
    @DisplayName("测试单个占位符替换")
    void testReplacePlaceholder() {
        // 正常情况
        String result = PlaceholderUtils.replacePlaceholder("Hello {{name}}!", "name", "World");
        assertEquals("Hello World!", result);

        // 空值情况
        String resultWithNull = PlaceholderUtils.replacePlaceholder("Hello {{name}}!", "name", null);
        assertEquals("Hello !", resultWithNull);

        // 多个相同占位符
        String resultMultiple = PlaceholderUtils.replacePlaceholder("{{name}} and {{name}}", "name", "Alice");
        assertEquals("Alice and Alice", resultMultiple);

        // 包含空格的占位符
        String resultWithSpaces = PlaceholderUtils.replacePlaceholder("Hello {{ name }}!", "name", "World");
        assertEquals("Hello World!", resultWithSpaces);

        // 文本为空
        String resultTextNull = PlaceholderUtils.replacePlaceholder(null, "name", "World");
        assertNull(resultTextNull);

        // 占位符为空
        String resultPlaceholderNull = PlaceholderUtils.replacePlaceholder("Hello {{name}}!", null, "World");
        assertEquals("Hello {{name}}!", resultPlaceholderNull);
    }

    @Test
    @DisplayName("测试多个占位符替换")
    void testReplacePlaceholdersMap() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        placeholders.put("age", "30");
        placeholders.put("city", "New York");

        String template = "Hello {{name}}, you are {{age}} years old and live in {{city}}.";
        String result = PlaceholderUtils.replacePlaceholders(template, placeholders);
        String expected = "Hello John, you are 30 years old and live in New York.";
        assertEquals(expected, result);

        // 测试空map
        String resultEmptyMap = PlaceholderUtils.replacePlaceholders(template, new HashMap<>());
        assertEquals(template, resultEmptyMap);

        // 测试null map
        String resultNullMap = PlaceholderUtils.replacePlaceholders(template, null);
        assertEquals(template, resultNullMap);

        // 测试null文本
        String resultNullText = PlaceholderUtils.replacePlaceholders(null, placeholders);
        assertNull(resultNullText);

        // 测试部分替换
        Map<String, String> partialPlaceholders = new HashMap<>();
        partialPlaceholders.put("name", "John");
        String partialResult = PlaceholderUtils.replacePlaceholders("Hello {{name}} and {{surname}}!", partialPlaceholders);
        assertEquals("Hello John and {{surname}}!", partialResult);
    }

    @Test
    @DisplayName("测试通过对象属性替换占位符")
    void testReplacePlaceholdersObject() {
        TestParams params = new TestParams("Alice", 25, "alice@example.com");
        String template = "Name: {{name}}, Age: {{age}}, Email: {{email}}";
        String result = PlaceholderUtils.replacePlaceholders(template, params);
        String expected = "Name: Alice, Age: 25, Email: alice@example.com";
        assertEquals(expected, result);

        // 测试不存在的字段
        String templateWithMissingField = "Name: {{name}}, Missing: {{missing}}";
        String resultMissingField = PlaceholderUtils.replacePlaceholders(templateWithMissingField, params);
        assertEquals("Name: Alice, Missing: {{missing}}", resultMissingField);

        // 测试null对象
        String resultNullObj = PlaceholderUtils.replacePlaceholders(template, null);
        assertEquals(template, resultNullObj);

        // 测试null文本
        String resultNullText = PlaceholderUtils.replacePlaceholders(null, params);
        assertNull(resultNullText);
    }

    @Test
    @DisplayName("测试验证占位符方法")
    void testValidatePlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("name", "John");
        placeholders.put("age", "30");

        // 全部占位符都有值
        assertTrue(PlaceholderUtils.validatePlaceholders("Hello {{name}}, you are {{age}}.", placeholders));

        // 存在未定义的占位符
        assertFalse(PlaceholderUtils.validatePlaceholders("Hello {{name}}, you are {{age}} and from {{city}}.", placeholders));

        // 空文本
        assertTrue(PlaceholderUtils.validatePlaceholders("", placeholders));
        assertTrue(PlaceholderUtils.validatePlaceholders(null, placeholders));

        // 空占位符map
        assertTrue(PlaceholderUtils.validatePlaceholders("Hello {{name}}.", new HashMap<>()));

        // null占位符map
        assertFalse(PlaceholderUtils.validatePlaceholders("Hello {{name}}.", null));

        // 没有占位符的文本
        assertTrue(PlaceholderUtils.validatePlaceholders("Hello World!", placeholders));
    }

    @Test
    @DisplayName("测试获取占位符列表方法")
    void testGetPlaceholders() {
        // 测试包含多个占位符的文本
        String text = "Hello {{name}}, you are {{age}} years old and live in {{city}}.";
        List<String> placeholders = PlaceholderUtils.getPlaceholders(text);
        assertEquals(3, placeholders.size());
        assertTrue(placeholders.contains("name"));
        assertTrue(placeholders.contains("age"));
        assertTrue(placeholders.contains("city"));

        // 测试包含重复占位符的文本
        String textWithDuplicates = "{{name}} and {{name}} are the same.";
        List<String> placeholdersWithDuplicates = PlaceholderUtils.getPlaceholders(textWithDuplicates);
        assertEquals(2, placeholdersWithDuplicates.size());

        // 测试包含空格的占位符
        String textWithSpaces = "Hello {{ name }}, welcome to {{ city }}!";
        List<String> placeholdersWithSpaces = PlaceholderUtils.getPlaceholders(textWithSpaces);
        assertEquals(2, placeholdersWithSpaces.size());
        assertTrue(placeholdersWithSpaces.contains("name"));
        assertTrue(placeholdersWithSpaces.contains("city"));

        // 测试没有占位符的文本
        String noPlaceholdersText = "Hello World!";
        List<String> noPlaceholders = PlaceholderUtils.getPlaceholders(noPlaceholdersText);
        assertEquals(0, noPlaceholders.size());

        // 测试null文本
        List<String> nullTextResult = PlaceholderUtils.getPlaceholders(null);
        assertEquals(0, nullTextResult.size());

        // 测试只有部分正确格式的占位符
        String malformedText = "{{incomplete {partial}} {{correct}}";
        List<String> malformedResult = PlaceholderUtils.getPlaceholders(malformedText);
        assertEquals(1, malformedResult.size());
        assertTrue(malformedResult.contains("correct"));
    }

    @Test
    @DisplayName("边界情况测试")
    void testEdgeCases() {
        // 测试只包含占位符的字符串
        String resultOnlyPlaceholder = PlaceholderUtils.replacePlaceholder("{{name}}", "name", "John");
        assertEquals("John", resultOnlyPlaceholder);

        // 测试空字符串
        String resultEmpty = PlaceholderUtils.replacePlaceholder("", "name", "John");
        assertEquals("", resultEmpty);

        // 测试占位符嵌套或特殊模式
        String complexText = "{{name}}{{name}} and {{ name }}{{ age }}";
        String resultComplex = PlaceholderUtils.replacePlaceholder(complexText, "name", "John");
        assertEquals("JohnJohn and {{ name }}{{ age }}", resultComplex);

        // 测试大量占位符
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("{{item").append(i).append("}} ");
        }
        String manyPlaceholders = sb.toString();
        Map<String, String> manyReplacements = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            manyReplacements.put("item" + i, "value" + i);
        }
        String resultMany = PlaceholderUtils.replacePlaceholders(manyPlaceholders, manyReplacements);
        assertTrue(resultMany.contains("value0"));
        assertTrue(resultMany.contains("value99"));
    }

    // 测试用的参数类
    static class TestParams {
        public String name;
        public int age;
        public String email;

        public TestParams(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }
}