# 🛒 Merchant NPC Mod (ExampleMod)

이 프로젝트는 **NeoForge 1.21.1** 환경에서 동작하는 커스텀 상인 NPC 모드입니다. 좀비의 외형을 가졌지만, 플레이어와 아이템을 거래할 수 있는 평화로운 상인을 추가합니다. 🧟‍♂️🤝

---

## 🌟 주요 기능 (Key Features)

### 1. 독특한 상인 엔티티
* **외형**: 좀비 모델(Zombie Model)을 베이스로 하여 기존 주민과는 차별화된 독특한 분위기를 연출합니다. 🎭
* **상태**: 플레이어를 공격하지 않으며, 우클릭을 통해 상호작용이 가능한 평화로운 몹입니다. ✨

### 2. 안정적인 거래 시스템
* 마인크래프트의 `AbstractVillager` 시스템을 계승하여 전용 GUI를 제공합니다. 📦
* **서버-클라이언트 동기화**: `mobInteract` 로직을 통해 서버에서 안전하게 거래 데이터를 처리합니다. 💻

---

## 🎮 게임 내 사용 방법 (Usage)

### 1. 상인 소환하기
상인을 게임에 불러오는 방법은 두 가지입니다.

* **크리에이티브 모드**: 인벤토리 내 **[생성 알]** 탭에서 전용 아이템인 `Merchant NPC Spawn Egg`를 찾아 땅에 사용하세요. 🥚
* **명령어**: 채팅창(`T`)을 열고 아래 명령어를 입력하여 즉시 소환할 수 있습니다. ⌨️
  `/summon examplemod:merchant_npc ~ ~ ~`

### 2. 상점 이용하기
소환된 상인을 **마우스 우클릭**하면 상점 UI가 나타납니다. 현재 설정된 거래 품목은 다음과 같습니다. 💰

| 거래 단계 | 지불 아이템 (Cost) | 결과 아이템 (Result) | 최대 거래 횟수 |
| :---: | :--- | :--- | :---: |
| **Step 1** | 💚 에메랄드 1개 | 💎 **다이아몬드 1개** | 12회 |
| **Step 2** | 💎 다이아몬드 3개 | 💚 **에메랄드 5개** | 8회 |

---

## 🛠️ 개발 및 빌드 환경 (Technical Info)

* **Minecraft Version**: 1.21.1 🧊
* **Modding SDK**: NeoForge 21.11.38-beta 🔥
* **Java Version**: JDK 21 ☕

### 핵심 클래스 설명
* **`MerchantNPC`**: 거래 품목 정의(`fillTrades`) 및 플레이어 상호작용 담당 클래스입니다.
* **`ExampleMod`**: 엔티티 능력치 등록 및 모드 전체 초기화를 담당합니다.

### 실행 방법
터미널에서 아래 명령어를 입력하여 모드를 실행할 수 있습니다. 🚀
`.\gradlew runClient`

---

## 📂 프로젝트 구조 (File Structure)
* `src/main/java/.../entity/MerchantNPC.java` - 엔티티 로직 📄
* `src/main/resources/assets/examplemod/` - 리소스 폴더 (텍스처/모델) 🎨
* `src/main/resources/data/` - 데이터 팩 설정 ⚙️