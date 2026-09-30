package com.tiffino.tiffino.repository;


import com.tiffino.tiffino.entity.GiftCard;
import com.tiffino.tiffino.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GiftCardRepo extends JpaRepository<GiftCard, Long> {
  //  List<GiftCard> findAllByUser(User user);

    List<GiftCard> findByUser(User user);

    // Check if the provided gift card is active and belongs to the user
    Optional<GiftCard> findByCodeAndActiveTrueAndUser(String code, User user);

    void deleteByUser(User user);
    GiftCard findByCodeAndUser(String code, User user);

  //  Optional<Object> findFirstByUserAndActiveTrue(User user);
    Optional<GiftCard> findFirstByUserAndActiveTrue(User user);



}
