package com.randioo.querytest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class FileQuery {
    private static String name = "C:\\Users\\asus\\Desktop\\1.out";

    @Test
    public void 总局数() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(name)))) {
            // FileReader reader = new FileReader(new
            // File("C:\\Users\\asus\\Desktop\\nohupqiaoma0927_1.out"));
            String str = null;

            int i = 0;
            String roomId = null;
            boolean s = true;
            int nullValue = 0;
            Map<String, Integer> map = new HashMap<>();
            while (s) {
                str = br.readLine();
                if (str == null) {
                    nullValue++;
                    if (nullValue > 100) {
                        int total = 0;
                        for (Map.Entry<String, Integer> entrySet : map.entrySet()) {
                            String v = entrySet.getKey();
                            Integer k = entrySet.getValue();
                            total += k;
                        }
//                        System.out.println(map);
                        System.out.println("总局数=" + total);
                        break;
                    }
                    continue;
                }

                if (i == 1) {
                    if (!str.contains("finishRoundCount:")) {
                        i = 0;
                        roomId = null;
                    } else {
                        String[] arr = str.split(": ");
                        int num = Integer.parseInt(arr[1]);

                        map.put(roomId, num);
                        // System.out.println(roomId + str);
                        i = 0;
                        roomId = null;
                    }
                }
                if (str.contains("roomId:")) {
                    roomId = str;
                    i++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void 活跃用户() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(name)))) {
            // FileReader reader = new FileReader(new
            // File("C:\\Users\\asus\\Desktop\\nohupqiaoma0927_1.out"));
            String str = null;

            boolean s = true;
            int nullValue = 0;
            Set<String> map = new HashSet<>();
            while (s) {
                str = br.readLine();
                if (str == null) {
                    nullValue++;
                    if (nullValue > 100) {
                        // System.out.println(map);
                        System.out.println("活跃用户=" + map.size());
                        break;
                    }
                    continue;
                }

                if (str.contains("SCFight") && str.contains("<ROLE>")) {
                    String account = substring(str, "account:", ",");
                    map.add(account);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String substring(String message, String key, String prefix) {
        int index_1 = message.indexOf(key) + key.length() + 1;
        for (int i = index_1 - 1; i < message.length(); i++) {
            String str = message.substring(i, i + prefix.length());
            if (str.equals(prefix)) {
                return message.substring(index_1 - 1, i);
            }
        }

        return null;
    }
}
