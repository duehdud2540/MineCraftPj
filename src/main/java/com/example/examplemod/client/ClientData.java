package com.example.examplemod.client; // 경로 확인!

import net.minecraft.core.BlockPos;
import java.util.ArrayList;
import java.util.List;

public class ClientData {
    // 서버에서 받아온 좌표와 이름을 클라이언트 메모리에 임시 보관하는 곳
    private static List<BlockPos> flags = new ArrayList<>();
    private static List<String> names = new ArrayList<>();

    // 패킷 핸들러가 이 메서드를 호출해서 데이터를 업데이트합니다.
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

    // 특정 인덱스의 이름을 안전하게 가져오는 메서드
    public static String getFlagName(int index) {
        if (index >= 0 && index < names.size()) {
            return names.get(index);
        }
        return "알 수 없는 지점 " + (index + 1);
    }
}