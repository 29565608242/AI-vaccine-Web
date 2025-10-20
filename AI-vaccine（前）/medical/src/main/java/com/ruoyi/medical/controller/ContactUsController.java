package com.ruoyi.medical.controller;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.medical.domain.ContactUs;
import com.ruoyi.medical.service.IContactUsService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 联系我们Controller
 * 
 * @author 唐宏伟
 * @date 2023-10-24
 */
@RestController
@RequestMapping("/medical/contactUs")
public class ContactUsController extends BaseController
{
    @Autowired
    private IContactUsService contactUsService;

    /**
     * 查询联系我们列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ContactUs contactUs)
    {
        startPage();
        List<ContactUs> list = contactUsService.selectContactUsList(contactUs);
        return getDataTable(list);
    }

    /**
     * 导出联系我们列表
     */
    @PreAuthorize("@ss.hasPermi('medical:contactUs:export')")
    @Log(title = "联系我们", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContactUs contactUs)
    {
        List<ContactUs> list = contactUsService.selectContactUsList(contactUs);
        ExcelUtil<ContactUs> util = new ExcelUtil<ContactUs>(ContactUs.class);
        util.exportExcel(response, list, "联系我们数据");
    }

    /**
     * 获取联系我们详细信息
     */
    @PreAuthorize("@ss.hasPermi('medical:contactUs:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contactUsService.selectContactUsById(id));
    }

    /**
     * 新增联系我们
     */
    @PreAuthorize("@ss.hasPermi('medical:contactUs:add')")
    @Log(title = "联系我们", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContactUs contactUs)
    {
        return toAjax(contactUsService.insertContactUs(contactUs));
    }

    /**
     * 修改联系我们
     */
    @PreAuthorize("@ss.hasPermi('medical:contactUs:edit')")
    @Log(title = "联系我们", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContactUs contactUs)
    {
        return toAjax(contactUsService.updateContactUs(contactUs));
    }

    /**
     * 删除联系我们
     */
    @PreAuthorize("@ss.hasPermi('medical:contactUs:remove')")
    @Log(title = "联系我们", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contactUsService.deleteContactUsByIds(ids));
    }
}
