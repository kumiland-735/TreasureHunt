package plugin.treasureHunt;

import java.io.InputStream;
import java.util.List;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import plugin.treasureHunt.mapper.data.PlayerScoreMapper;
import plugin.treasureHunt.mapper.data.data.PlayerScore;

/**
 * データベースやそれに付随する登録や更新処理を行うクラスです。
 */
public class PlayerScoreData {
  private final SqlSessionFactory sqlSessionFactory;
  private final PlayerScoreMapper mapper;

  public PlayerScoreData() {
    try {
      InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
      sqlSessionFactory = (new SqlSessionFactoryBuilder()).build(inputStream);
      SqlSession session = sqlSessionFactory.openSession(true);
      mapper = session.getMapper(PlayerScoreMapper.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * スコアテーブルから一覧でスコア情報を取得する。
   * @return  スコア情報の一覧
   */
  public List<PlayerScore> selectList() {
    return mapper.selectList();
  }

  /**
   * スコアテーブルにスコア情報を登録する。
   * @param playerScore  プレイヤースコア
   */
  public void insert(PlayerScore playerScore) {
    mapper.insert(playerScore);
  }

}
