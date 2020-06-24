package cn.flyingocean.fileship.repository;

import cn.flyingocean.fileship.domain.ChineseWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChineseWordRepository extends JpaRepository<ChineseWord,Integer>{

    ChineseWord findByText(String text);
}
