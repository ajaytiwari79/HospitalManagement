package com.kairos.controller.clause;

import static com.kairos.constant.ApiConstant.API_CLAUSES_URL;


import com.kairos.custome_exception.DataNotExists;
import com.kairos.persistance.model.clause.Clause;
import com.kairos.dto.ClauseDto;
import com.kairos.persistance.model.clause.dto.ClauseGetQueryDto;
import com.kairos.service.clause.ClauseService;
import com.kairos.service.clause.paginated_result_service.PaginatedResultsRetrievedEvent;
import com.kairos.utils.ResponseHandler;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.inject.Inject;
import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;

@RestController
@RequestMapping(API_CLAUSES_URL)
@Api(API_CLAUSES_URL)
@CrossOrigin
public class ClauseController {


    @Inject
    private ClauseService clauseService;

    @Inject
    ApplicationEventPublisher eventPublisher;


    @ApiOperation("add new clause")
    @PostMapping("/add_clause")
    public ResponseEntity<Object> createClause(@Validated @RequestBody ClauseDto clauseDto) throws RepositoryException {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.createClause(clauseDto));
    }

    /*@ApiOperation("get clause by organization type")
    @GetMapping("/byorganizationType")
    public ResponseEntity<Object> getClauseByOrganizationType(@RequestParam String organizationType) {
        Map<String, Object> result = new HashMap<>();
        if (!Optional.ofNullable(organizationType).isPresent()) {
            return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "organizationtype cannot be null or empty");
        } else {
            result = clauseService.getClauseByOrganizationType(organizationType);
            return ResponseHandler.generateResponse(HttpStatus.OK, true, result.get("data"));
        }
    }
*/
    @ApiOperation("get clause by id")
    @GetMapping("/clause/id/{id}")
    public ResponseEntity<Object> getClauseById(@PathVariable BigInteger id) {
        if (id!=null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseById(id));

        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST, false, "id cannot be null  or empty");

    }

   /* @ApiOperation("get clause by account type")
    @GetMapping("/byAccount")
    public ResponseEntity<Object> getClauseByAccountType(@RequestParam String accountType) {
        if (StringUtils.isEmpty(accountType)) {
            throw new NullPointerException("AccountType Cannot be Null or Empty");
        } else
            return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClauseByAccountType(accountType));

    }
*/

    @ApiOperation("get clause by multi select")
    @PostMapping("/clause")
    public ResponseEntity<Object> getClause(@RequestBody ClauseGetQueryDto clauseQueryDto) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClause(clauseQueryDto));
    }

    @ApiOperation("delete clause by id")
    @DeleteMapping("/delete/id/{id}")
    public ResponseEntity<Object> deleteClause(@PathVariable BigInteger id) {
        if (id!=null)
        {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.deleteClause(id));
    }
    else
        return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST,false,"id cannot be null or empty");

    }

    @ApiOperation("update clause description")
    @PutMapping("/update/clause/{clauseId}")
    public ResponseEntity<Object> updateClause(@PathVariable BigInteger clauseId,@Validated @RequestBody ClauseDto clauseDto) throws RepositoryException {

        if (clauseId!=null) {
            return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.updateClause(clauseId, clauseDto));
        }
        return ResponseHandler.invalidResponse(HttpStatus.BAD_REQUEST,false,"clauseId cannot be null or empty");
    }


    @ApiOperation("get clause by list")
    @PostMapping("/clause/list")
    public ResponseEntity<Object> getClausesByIds(@RequestBody List<BigInteger> clausesids) {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getClausesByIds(clausesids));
    }

    @ApiOperation("get All clauses")
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllClauses() {
        return ResponseHandler.generateResponse(HttpStatus.OK, true, clauseService.getAllClauses());
    }


    @ApiOperation("default clauses page with default size 10 ")
    @GetMapping("/page/clause")
    public Page<Clause> getClausePagination(@RequestParam int page, @RequestParam(defaultValue = "10") int size, UriComponentsBuilder uriComponentsBuilder
            , HttpServletResponse httpServletResponse) {
        Page<Clause> resultPage = clauseService.getClausePagination(page, size);
        if (page > resultPage.getTotalPages()) {
            throw new DataNotExists("Clauses Not for Page" + page);
        }
        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent(uriComponentsBuilder, httpServletResponse, Clause.class, page, resultPage.getTotalPages(), size));
        return resultPage;
    }


}