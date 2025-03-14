package plugin.treasureHunt.command;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.SplittableRandom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import plugin.treasureHunt.PlayerScoreData;
import plugin.treasureHunt.mapper.data.data.PlayerScore;

/**
 * 宝箱(チェスト)にランダムに設置されるリンゴを見つけて、スコアを獲得するゲームを起動するコマンドです。
 * スコアはリンゴの種類と獲得時間によって変わります。
 * 結果は、プレイヤー名、スコア、日時で保存されます。
 */

public class TreasureHuntCommand extends BaseCommand implements Listener {

  private final PlayerScoreData playerScoreData = new PlayerScoreData();
  private final List<Location> chestLocations = new ArrayList<>();
  private Location appleLocation;
  private Location goldenAppleLocation;
  private long startTime;
  private boolean gameRunning;


  @Override
  public boolean onExecutePlayerCommand(Player player, Command command, String label, String[] args) {

    if (args.length == 1 && "list".equals(args[0])){
      this.showPlayerScoreList(player);
      return false;

    } else {
      startGame(player);
      return true;
    }
  }

  @Override
  public boolean onExecuteNPCCommand(CommandSender sender, Command command, String label, String[] args) {
    return false;
  }
  /**
   * 現在登録されているスコアの一覧をメッセージに表示する。
   * @param player  プレイヤー
   */
  private void showPlayerScoreList(Player player) {
    for(PlayerScore playerScore : this.playerScoreData.selectList()) {
      int id = playerScore.getId();
      player.sendMessage(id + " | " + playerScore.getPlayerName() + " | " + playerScore.getScore() + " | " + playerScore.getRegisteredAt().format(
          DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  /**
   * ゲームを開始し、プレイヤーに初期設定とメッセージを表示します。
   * @param player  ゲームを開始するプレイヤー
   */
  private void startGame(Player player) {
    gameRunning = true;
    startTime = System.currentTimeMillis();
    setupChests(player);
    player.sendTitle("ゲーム開始！", "宝箱をクリックして、リンゴを見つけよう！",0,60,0);
    player.sendMessage("5秒以内・・・100点");
    player.sendMessage("10秒以内・・・50点");
    player.sendMessage("金のリンゴならボーナス200点！");
  }

  /**
   * プレイヤーの周囲に宝箱(チェスト)を設置します。
   * @param player  チェストを設置する中心となるプレイヤー
   * *
   * プレイヤーの現在位置を中心とし、円形に12個のチェストを配置
   * 配置されたチェストの中からランダムに一つを選び、「リンゴ」を追加
   * 配置されたチェストの中から別のランダムな一つを選び、「金のリンゴ」を追加
   */
  private void setupChests(Player player) {
    World world = player.getWorld();
    Location center = player.getLocation();

    int radius = 10;
    int chestCount = 12;

    for(int i=0; i < chestCount; i++){
      double angle = 2 * Math.PI * i / chestCount;
      int x = (int) (center.getX() + radius * Math.cos(angle));
      int z = (int) (center.getZ() + radius * Math.sin(angle));
      int y = world.getHighestBlockYAt(x, z);

      Location chestLocation = new Location(world, x, y+1, z);
      chestLocations.add(chestLocation);
      world.getBlockAt(chestLocation).setType(Material.CHEST);
    }

    SplittableRandom random = new SplittableRandom();
    appleLocation = chestLocations.get(random.nextInt(chestCount));
    do {goldenAppleLocation = chestLocations.get(random.nextInt(chestCount));
    } while (Objects.equals(goldenAppleLocation,appleLocation));

    placeItemInChest(appleLocation, Material.APPLE);
    placeItemInChest(goldenAppleLocation, Material.GOLDEN_APPLE);
  }

  /**
   * 指定された位置にある宝箱(チェスト)に、指定されたアイテムを追加します。
   * @param location  チェストの位置
   * @param material  チェストに追加するアイテムの種類
   */
  private void placeItemInChest(Location location, Material material) {
    Block block = location.getBlock();
    if (block.getState() instanceof Chest chest) {
      chest.getInventory().addItem(new ItemStack(material));
      chest.update();
    }
  }

  /**
   * ゲーム中にプレイヤーが宝箱をクリックした際の動作を処理します。
   * @param event  プレイヤーの操作イベント
   */
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!gameRunning) return;

    Player player = event.getPlayer();
    Block clickedBlock = event.getClickedBlock();

    if (clickedBlock == null) {
      return;
    }

    Location clickedLocation = clickedBlock.getLocation();

    if (clickedLocation.equals(appleLocation)) {
      clickedLocation.getWorld().dropItem(clickedLocation.add(0.5, 1, 0.5), new ItemStack(Material.APPLE));
      endGame(player, false);
    } else if (clickedLocation.equals(goldenAppleLocation)) {
      clickedLocation.getWorld().dropItem(clickedLocation.add(0.5, 1, 0.5), new ItemStack(Material.GOLDEN_APPLE));
      endGame(player, true);
    } else {
      player.sendMessage("おしい！");
    }
  }

  /**
   * ゲームを終了し、プレイヤーのスコアを計算して表示します。
   * @param player  プレイヤー
   * @param foundGoldenApple  プレイヤーがボーナス(金のリンゴ)を見つけたかどうか
   */
  private void endGame(Player player, boolean foundGoldenApple) {
    gameRunning = false;
    long endTime = System.currentTimeMillis();
    long timeTaken = (endTime - startTime) / 1000;

    int score;
    if (foundGoldenApple) {
      score = 200;
    } else if (timeTaken <= 5) {
      score = 100;
    } else if (timeTaken <= 10) {
      score = 50;
    } else {
      score = 0;
    }
    player.sendTitle("ゲーム終了！", player.getName() + " " + score + "点！",0,100,0);
    this.playerScoreData.insert(new PlayerScore(player.getName(),score));

    removeChests();
  }

  /**
   * ゲーム終了後、残っている宝箱(チェスト)を消去します。
   */
  private void removeChests() {
    chestLocations.stream().map(Location::getBlock)
        .filter(block -> block.getType() == Material.CHEST)
        .forEach(block -> block.setType(Material.AIR));
    chestLocations.clear();
  }
}
