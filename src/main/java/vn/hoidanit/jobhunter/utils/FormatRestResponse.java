package vn.hoidanit.jobhunter.utils;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.response.RestResponse;
import vn.hoidanit.jobhunter.utils.anotations.ApiMessage;

@RestControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

	// @ResponseBodyAdivce cho phép bạn chăn

	@Override
	public boolean supports(MethodParameter returnType, Class converterType) {
		// khi nao muon phan hoi , khi nao muon ghi de => return true => bat cu phan hoi
		// nao cung lam v
		// tra true -> chay xuong func beforeBodyWrite
		return true;
	}

	@Override
	public Object beforeBodyWrite(
			Object body,
			MethodParameter returnType,
			MediaType selectedContentType,
			Class selectedConverterType,
			ServerHttpRequest request,
			ServerHttpResponse response) {

		//
		HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
		int status = servletResponse.getStatus();

		// truoc khi tra ra moi response deu boc qua cai nay
		RestResponse<Object> res = new RestResponse<Object>();
		res.setStatusCode(status);

		if (body instanceof String || body instanceof Resource) {
			return body;
		}
		// khong can thiep swagger
		String path = request.getURI().getPath();
		if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui")) {
			return body;
		}

		if (status >= 400) {
			// case error
			return body;

		} else {

			// case success
			// toi day la controller da tra ve du lieu , sau do minh format chung 1 kieu tra
			// ve (chi co the sua doi body)
			// ve
			ApiMessage msg = returnType.getMethodAnnotation(ApiMessage.class);
			res.setMessage(msg != null ? msg.value() : "Call Api Success !");
			res.setData(body);

		}

		return res;
	}

}
