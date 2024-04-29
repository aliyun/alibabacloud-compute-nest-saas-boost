package org.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.common.BaseResult;
import org.example.common.model.UserInfoModel;
import org.example.common.param.cert.DeleteCertParam;
import org.example.common.param.cert.PutCertParam;
import org.example.service.certificate.CertificateService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


@Slf4j
@RestController
@RequestMapping("/api")
@Api(value="cert",tags={"cert"})
public class CertificateController {
    @Resource
    private CertificateService certificateService;

    @ApiOperation(value = "上传证书", nickname = "putCert")
    @RequestMapping(path = "/putCert", method = RequestMethod.POST)
    public BaseResult<Boolean> putCert(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                       @RequestBody PutCertParam putCertParam) {
        return certificateService.putCert(userInfoModel, putCertParam);
    }

    @ApiOperation(value = "删除证书", nickname = "deleteCert")
    @RequestMapping(value = "/deleteCert", method = RequestMethod.POST)
    public BaseResult<Boolean> deleteCert(@ApiIgnore @AuthenticationPrincipal UserInfoModel userInfoModel,
                                          @RequestBody DeleteCertParam deleteCertParam) {
        return certificateService.deleteCert(userInfoModel, deleteCertParam);
    }
}


