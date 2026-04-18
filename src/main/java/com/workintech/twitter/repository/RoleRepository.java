package com.workintech.twitter.repository;

import com.workintech.twitter.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Bunu import etmeyi unutma!

public interface RoleRepository extends JpaRepository<Role, Long> {


    //authority alanına göre role arıyorum
    //ama bu role her zaman bulunmayabiliyo o yüzden Optional döndürüyorum
    Optional<Role> findByAuthority(String authority);
}