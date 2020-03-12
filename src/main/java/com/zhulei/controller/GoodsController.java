package com.zhulei.controller;

import com.zhulei.domain.MiaoshaUser;
import com.zhulei.redis.GoodsKey;
import com.zhulei.redis.RedisService;
import com.zhulei.service.GoodsService;
import com.zhulei.service.MiaoshaUserService;
import com.zhulei.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

	@Autowired
	MiaoshaUserService userService;
	
	@Autowired
	RedisService redisService;

	@Autowired
	GoodsService goodsService;

	@Autowired
	ThymeleafViewResolver thymeleafViewResolver;

	@Autowired
	ApplicationContext applicationContext;

	/**
	 * QPS:214.2
	 * 3000*10

	 */
	@RequestMapping(value="/to_list", produces="text/html")
	@ResponseBody
	public String list(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user) {
		model.addAttribute("user", user);
		//取缓存
		String html = redisService.get(GoodsKey.getGoodsList, "", String.class);
		if(!StringUtils.isEmpty(html)) {
			return html;
		}
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
//    	 return "goods_list";
		SpringWebContext ctx = new SpringWebContext(request,response,
				request.getServletContext(),request.getLocale(), model.asMap(), applicationContext );
		//手动渲染
		html = thymeleafViewResolver.getTemplateEngine().process("goods_list", ctx);
		if(!StringUtils.isEmpty(html)) {
			redisService.set(GoodsKey.getGoodsList, "", html);
		}
		return html;
	}

	@RequestMapping("/to_detail/{goodsId}")
	public String detail(Model model, MiaoshaUser user,
						 @PathVariable("goodsId")long goodsId) {
		model.addAttribute("user", user);
		//查询商品详情
		GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
		model.addAttribute("goods",goods);
		Long startAt = goods.getStartDate().getTime();
		long endAt = goods.getEndDate().getTime();
		long now = System.currentTimeMillis();

		int miaoshaStatus=0;
		int remainSeconds=0;

		if(now<startAt){
			//秒杀没开始倒计时
			miaoshaStatus=0;
			remainSeconds=(int)(startAt-now)/1000;
		}else if(now>endAt){
			//秒杀已结束
			miaoshaStatus=2;
			remainSeconds=-1;
		}else{
			//秒杀进行时
			miaoshaStatus=1;
			remainSeconds=0;
		}
		model.addAttribute("miaoshaStatus",miaoshaStatus);
		model.addAttribute("remainSeconds",remainSeconds);
		return "goods_detail";
	}


}
