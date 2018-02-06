/*
 * Copyright (c) 2018, org.smartboot. All rights reserved.
 * project name: smart-socket
 * file name: URICheckFilter.java
 * Date: 2018-02-06
 * Author: sandao
 */

package org.smartboot.socket.http.rfc2616;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.smartboot.socket.http.HttpHeaderNames;
import org.smartboot.socket.http.HttpRequest;
import org.smartboot.socket.http.HttpResponse;
import org.smartboot.socket.http.enums.HttpStatus;

/**
 * RFC2616 3.2.1
 * HTTP 协议不对 URI 的长度作事先的限制，服务器必须能够处理任何他们提供资源的 URI，并 且应该能够处理无限长度的 URIs，这种无效长度的 URL 可能会在客户端以基于 GET 方式的 请求时产生。如果服务器不能处理太长的 URI 的时候，服务器应该返回 414 状态码(此状态码 代表 Request-URI 太长)。
 * 注:服务器在依赖大于 255 字节的 URI 时应谨慎，因为一些旧的客户或代理实现可能不支持这 些长度。
 *
 * @author 三刀
 * @version V1.0 , 2018/2/6
 */
public class URICheckFilter extends CheckFilter {
    public static final int MAX_LENGTH = 255 * 1024;
    private static final Logger LOGGER = LogManager.getLogger(URICheckFilter.class);

    @Override
    public void doFilter(HttpRequest request, HttpResponse response) {

        if (StringUtils.length(request.getUri()) > MAX_LENGTH) {
            response.setHttpStatus(HttpStatus.URI_TOO_LONG);
            return;
        }
        /**
         *
         *1. 如果 Request-URI 是绝对地址(absoluteURI)，那么主机(host)是 Request-URI 的 一部分。任何出现在请求里 Host 头域的值应当被忽略。
         *2. 假如 Request-URI 不是绝对地址(absoluteURI)，并且请求包括一个 Host 头域，则主 机(host)由该 Host 头域的值决定.
         *3. 假如由规则1或规则2定义的主机(host)对服务器来说是一个无效的主机(host)， 则应当以一个 400(坏请求)错误消息返回。
         */
        String uri = request.getUri();//TODO
        String host = null;
        String headHost = request.getHeader(HttpHeaderNames.HOST);
        boolean absoulute = false;//是否绝对路径
        if (absoulute) {
            if (!StringUtils.isBlank(headHost)) {
                LOGGER.debug("absoulute uri:{} ,ignore HEAD HOST:{}", uri, headHost);
            }
        } else {
            host = headHost;
        }
        //规则3校验暂时只校验是否存在Host
        if (StringUtils.isBlank(host)) {
            response.setHttpStatus(HttpStatus.BAD_REQUEST);
            return;
        }
        doNext(request, response);
    }
}