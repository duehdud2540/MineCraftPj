package com.example.examplemod.client; // 경로 확인!

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class ClientData {
    private static List<BlockPos> flags = new ArrayList<>();
    private static List<String> names = new ArrayList<>();

    public static void updateFlags(List<BlockPos> newFlags, List<String> newNames) {
        flags = newFlags;
        names = newNames;
    }

    public static List<BlockPos> getFlags() {
        return flags;
    }

    public static List<String> getNames() {
        return names;
    }

    public static String getFlagName(int index) {
        if (index >= 0 && index < names.size()) {
            return names.get(index);
        }
        return "알 수 없는 지점 " + (index + 1);
    }
}