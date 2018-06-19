package com.kairos.persistance.repository.clause;

import com.kairos.persistance.model.clause.Clause;
import com.kairos.persistance.repository.custom_repository.MongoBaseRepository;
import com.kairos.response.dto.clause.ClauseResponseDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface ClauseMongoRepository extends MongoRepository<Clause,BigInteger>,CustomClauseRepository{


    @Query("{deleted:false,countryId:?0,title:{$regex:?1,$options:'i'}}")
    Clause findByTitleAndCountry(Long countryId,String title);

    Clause findByid(BigInteger id);

    @Query("{deleted:false,countryId:?0,_id:?1}")
    Clause findByIdAndNonDeleted(Long countryId,BigInteger id);

    @Query("{deleted:false,countryId:?0,title:?1}")
    Clause findClauseByNameAndCountryId(Long countryId,String title);

    @Query("{deleted:false,countryId:?0}")
    List<ClauseResponseDto>  findAllClause(Long countryId);

    @Query("{deleted:false,countryId:?0,_id:{$in:?1}}")
    List<Clause>  getClauseListByIds(Long countryId, Set<BigInteger> ids);

    @Query("{accountType:?0}")
    List<Clause> getClauseByAccountType(String accountType);


}
