package com.workintech.twitter.util.mapper;

import com.workintech.twitter.dto.response.LikeResponseDto;
import com.workintech.twitter.entity.Like;
import org.springframework.stereotype.Component;

@Component
public class LikeMapper {

    //entity yi dto çeviriyorum (frontende like bilgisini göndermek için)
    public LikeResponseDto toResponseDto(Like like) {
        return new LikeResponseDto(
                like.getId(),
                like.getUser().getUsername(),
                like.getTweet().getId()
        );
    }

//burda DTO’yu entity’e çevirmiyorum çünkü gerekli bilgileri zaten service içinde oluşturuluyo,
//mapperı da sadece entity yi frontende göndereceğim DTOya çevirmek için kullanıyorum.
}
