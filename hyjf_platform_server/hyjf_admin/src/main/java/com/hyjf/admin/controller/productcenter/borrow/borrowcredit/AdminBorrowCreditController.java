package com.hyjf.admin.controller.productcenter.borrow.borrowcredit;

import com.hyjf.admin.beans.BorrowCreditListResultBean;
import com.hyjf.admin.beans.request.BorrowCreditRequest;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.service.BorrowCreditService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * admin: 汇直投：债权转让
 * @author zhangyk
 * @date 2018/7/9 14:59
 */

@Api(value = "产品中心-汇转让")
@RestController
@RequestMapping("/borrow/credit")
public class AdminBorrowCreditController {


    @Autowired
    private BorrowCreditService borrowCreditService;


    @ApiOperation(value = "债权转让", notes = "债权转让")
    @PostMapping("/getList")
    @ResponseBody
    public Object  getBorrowCreditList(@RequestBody BorrowCreditRequest request){
        AdminResult result = borrowCreditService.getBorrowCreditList(request);
        return result;
    }
}
