package com.zhulei.controller;

import com.zhulei.domain.MiaoshaUser;
import com.zhulei.redis.RedisService;
import com.zhulei.service.GoodsService;
import com.zhulei.service.MiaoshaUserService;
import com.zhulei.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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

	@RequestMapping("/to_list")
	public String list(Model model,MiaoshaUser user) {
		model.addAttribute("user", user);
		//查询商品列表
		List<GoodsVo> goodsList = goodsService.listGoodsVo();
		model.addAttribute("goodsList", goodsList);
		return "goods_list";
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
