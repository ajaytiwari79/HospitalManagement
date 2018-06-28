package com.kairos.service.clause.paginated_result_service;

import org.springframework.context.ApplicationEvent;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

public class PaginatedResultsRetrievedEvent extends ApplicationEvent {

   private UriComponentsBuilder uriBuilder;
    private Class clazz;
    private  HttpServletResponse httpServletResponse;
    private  int page;
    private int totalPage;
    private int pageSize;

    public UriComponentsBuilder getUriBuilder() {
        return uriBuilder;
    }

    public Class getClazz() {
        return clazz;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }





    public PaginatedResultsRetrievedEvent(Object source)
    {
        super(source);
    }
    public PaginatedResultsRetrievedEvent(UriComponentsBuilder uriBuilder,HttpServletResponse httpServletResponse,Class clazz
    ,int page,int totalPage,int pageSize)
    {
this(new String("he"));
        this.uriBuilder=uriBuilder;
this.clazz=clazz;
this.httpServletResponse=httpServletResponse;
this.page=page;
this.totalPage=totalPage;
this.pageSize=pageSize;

    }
}
