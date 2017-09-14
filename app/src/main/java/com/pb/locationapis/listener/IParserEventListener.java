package com.pb.locationapis.listener;


import com.pb.locationapis.model.bo.routes.RoutesBO;
import com.pb.locationapis.parser.ParserType;

/**
 * Created by NEX7IMH on 22-June-17.
 */
public interface IParserEventListener
{
    /**
     * @param parserType
     */
    public void onParseCompleted(ParserType parserType, RoutesBO routesBO);

}
