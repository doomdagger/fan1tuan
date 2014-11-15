package com.fan1tuan.adminshop.ui.struts2.core;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import com.fan1tuan.admin.dto.Page;
import com.fan1tuan.adminshop.business.AdminShopService;
import com.fan1tuan.adminshop.util.AdminConstant;
import com.fan1tuan.general.dao.Pageable;
import com.fan1tuan.general.ui.struts2.core.support.Fan1TuanAction;
import com.fan1tuan.general.util.UUIDGenerator;
import com.fan1tuan.order.pojos.Order;
import com.fan1tuan.shop.pojos.Shop;
import com.fan1tuan.shop.pojos.ShopTasteTag;
import com.opensymphony.xwork2.Action;

public class AdminShopMain extends Fan1TuanAction{

	private static final long serialVersionUID = 1L;
	
	//base params
	private String navName = "adminshop";
	//common params
	private AdminShopService adminShopService;
	private List<Shop> shopList;//出参:showShopList doShopAdd
	private String subTitle="";//出参：showShopAdd
	private String shopId;//出参：showShopEdit,doShopDelete,showShopOrders
	private String shopName;//param-out
	
	
	//--------------店铺列表-----------------
	private int shopPage;//param-in
	private Page shopListPage;//param-out
	public String showShopList(){
		setShopList(adminShopService.getShopsInPage(Pageable.inPage((getShopPage()==0?0:getShopPage()-1), AdminConstant.SHOPLIST_PAGESIZE)));
		//set page
		Page page = new Page();
		page.setPageCount(adminShopService.getShopPageCount(AdminConstant.SHOPLIST_PAGESIZE));//设置分页数量
		page.setCurrentPage((getShopPage()==0)?1:getShopPage());//设置当前分页
		setShopListPage(page);
		return Action.SUCCESS;
	}
	
	//--------------显示添加店铺-------------
	public String showShopAdd(){
		setSubTitle("添加新店铺");
		return Action.SUCCESS;
	}
	
	
	//----------------确认店铺添加-----------
	//入参:doShopAdd,doShopEdit
	private String name;
	private String description;
	private double deliveryCharge;
	private double avgPersonCost;
	private double avgDeliveryTime;
	private String cellphone;
	private int shopType;
	private String opentime;
	private String closetime;
	private String address;
	private double latitude;
	private double longtitude;
	private File avatar;
	private String avatarFileName;
	//出参-shopList
	public String doShopAdd() throws ParseException, IOException{
		Shop shop = new Shop();
		shop.setName(getName());
		shop.setDescription(getDescription());
		shop.setAvgPersonCost(getAvgPersonCost());
		shop.setAvgDeliveryTime(getAvgDeliveryTime());
		shop.setCellphone(getCellphone());
		shop.setType(getShopType());
		shop.setDeliveryCharge(getDeliveryCharge());
		SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm");
		shop.setOpenTime(formatDate.parse(getOpentime()));
		shop.setCloseTime(formatDate.parse(getClosetime()));
		shop.setAddress(getAddress());
		double[] location = {getLongtitude(),getLatitude()};
		shop.setLocation(location);
		shop.setCreateTime(new Date());
		//upload image file
		if(getAvatar()!=null){
			setAvatarFileName(UUIDGenerator.generateUUID()+getAvatarFileName().substring(getAvatarFileName().indexOf(".")));//重置文件名
			String destPath = ServletActionContext.getServletContext().getRealPath(AdminConstant.UPLOAD_SHOP_PATH);
	        File dest = new File(destPath, getAvatarFileName()); //服务器的文件
	        FileUtils.copyFile(avatar, dest);//完成了文件的拷贝工作
	        shop.setAvatar(AdminConstant.IMG_SAVE_PREFFIX+AdminConstant.UPLOAD_SHOP_PATH+"/"+getAvatarFileName());
		}
        //end upload file
		adminShopService.addNewShop(shop);
		return Action.SUCCESS;
	}
	
	//-----------------显示编辑店铺------------
	private Shop shop;
	public String showShopEdit(){
		
		Shop shop = adminShopService.getOneShopByShopId(getShopId());
		setShop(shop);
		setSubTitle("编辑店铺:"+shop.getName());
		return Action.SUCCESS;
	}
	//------------------确认编辑店铺--------------
	public String doShopEdit() throws ParseException, IOException{
		Shop shop = adminShopService.getOneShopByShopId(getShopId());
		//shop.setId(getShopId());
		shop.setName(getName());
		shop.setDescription(getDescription());
		shop.setAvgPersonCost(getAvgPersonCost());
		shop.setAvgDeliveryTime(getAvgDeliveryTime());
		shop.setCellphone(getCellphone());
		shop.setType(getShopType());
		shop.setDeliveryCharge(getDeliveryCharge());
		SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm");
		shop.setOpenTime(formatDate.parse(getOpentime()));
		shop.setCloseTime(formatDate.parse(getClosetime()));
		shop.setAddress(getAddress());
		double[] location = {getLongtitude(),getLatitude()};
		shop.setLocation(location);
		if(shop.getCreateTime()==null){shop.setCreateTime(new Date());}
		//upload image file
		if(getAvatar()!=null){
			String destPath = ServletActionContext.getServletContext().getRealPath(AdminConstant.UPLOAD_SHOP_PATH);  
	        File dest = new File(destPath, getAvatarFileName()); //服务器的文件
	        FileUtils.copyFile(avatar, dest);//完成了文件的拷贝工作 
	        shop.setAvatar(AdminConstant.IMG_SAVE_PREFFIX+AdminConstant.UPLOAD_SHOP_PATH+"/"+getAvatarFileName());
		}
        //end upload file
		adminShopService.saveShopEdit(shop);
		return Action.SUCCESS;
	}
	
	//-------------------删除一个店铺------------
	public String doShopDelete(){
		adminShopService.deleteShopByShopId(getShopId());
		return Action.SUCCESS;
	}
	
	
	//------------------------show all orders of a shop--------------
	//shopId : param-in
	private int orderPage;//param-in
	private List<Order> orderList;//param-out
	private Page orderListPage;//param-out
	public String showShopOrders(){
		//setOrderPage(getOrderPage());
		setOrderList(adminShopService.getShopOrdersByShopIdInPage(shopId, Pageable.inPage((getOrderPage()==0?0:getOrderPage()-1), AdminConstant.ORDERLIST_PAGESIZE)));
		//set the page
		Page page = new Page();
		page.setPageCount(adminShopService.getOrderPageCount(getShopId(), AdminConstant.ORDERLIST_PAGESIZE));
		page.setCurrentPage((getOrderPage()==0)?1:getOrderPage());
		setOrderListPage(page);
		//set a shopName & shopId
		setShopName(adminShopService.getOneShopByShopId(getShopId()).getName());
		setShopId(getShopId());
		return Action.SUCCESS;
	}
	//---------------------------do delete order---------------------
	private String orderId;//param-in
	public String doShopOrderDelete(){
		adminShopService.deleteShopOrderByOrderId(getOrderId());
		return Action.SUCCESS;
	}

	
	//----------------显示店铺口味标签列表-------------------------
	private List<ShopTasteTag> shopTasteTagList;//param-out
	public String showShopTags(){
		setShopTasteTagList(adminShopService.getShopTasteTags());
		return Action.SUCCESS;
	}
	
	//----------------添加店铺口味标签列表------------------------
	private String tagName;//param-in
	private String tagDescription;//param-in
	public String doShopTagAdd(){
		ShopTasteTag shopTasteTag = new ShopTasteTag();
		shopTasteTag.setDescription(getTagDescription());
		shopTasteTag.setName(getTagName());
		adminShopService.addNewShopTasteTag(shopTasteTag);
		return Action.SUCCESS;
	}
	//-----------------编辑口味标签---------------------------
	private String tagId;
	public String doShopTagEdit(){
		ShopTasteTag shopTasteTag = new ShopTasteTag();
		shopTasteTag.setDescription(getTagDescription());
		shopTasteTag.setName(getTagName());
		shopTasteTag.setId(getTagId());
		adminShopService.saveShopTasteTagEdit(shopTasteTag);
		return Action.SUCCESS;
	}
	
	//-------------------删除口味标签---------------------
	public String doShopTagDelete(){
		adminShopService.deleteShopTasteTagByTagId(getTagId());
		return Action.SUCCESS;
	}
	
	
	
	
	
	
	//-------------getters and setters------------------

	public List<Shop> getShopList() {
		return shopList;
	}

	public void setShopList(List<Shop> shopList) {
		this.shopList = shopList;
	}
	
	public AdminShopService getAdminShopService() {
		return adminShopService;
	}
	public void setAdminShopService(AdminShopService adminShopService) {
		this.adminShopService = adminShopService;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getAvgPersonCost() {
		return avgPersonCost;
	}

	public void setAvgPersonCost(double avgPersonCost) {
		this.avgPersonCost = avgPersonCost;
	}

	public String getCellphone() {
		return cellphone;
	}

	public void setCellphone(String cellphone) {
		this.cellphone = cellphone;
	}

	public int getShopType() {
		return shopType;
	}

	public void setType(int shopType) {
		this.shopType = shopType;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
	}

	public String getClosetime() {
		return closetime;
	}

	public void setClosetime(String closetime) {
		this.closetime = closetime;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(double longtitude) {
		this.longtitude = longtitude;
	}

	public File getAvatar() {
		return avatar;
	}

	public void setAvatar(File avatar) {
		this.avatar = avatar;
	}

	public double getAvgDeliveryTime() {
		return avgDeliveryTime;
	}

	public void setAvgDeliveryTime(double avgDeliveryTime) {
		this.avgDeliveryTime = avgDeliveryTime;
	}

	public String getNavName() {
		return navName;
	}

	public void setNavName(String navName) {
		this.navName = navName;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public double getDeliveryCharge() {
		return deliveryCharge;
	}

	public void setDeliveryCharge(double deliveryCharge) {
		this.deliveryCharge = deliveryCharge;
	}

	public List<Order> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<Order> orderList) {
		this.orderList = orderList;
	}

	public int getOrderPage() {
		return orderPage;
	}

	public void setOrderPage(int orderPage) {
		this.orderPage = orderPage;
	}

	public int getShopPage() {
		return shopPage;
	}

	public void setShopPage(int shopPage) {
		this.shopPage = shopPage;
	}

	public Page getOrderListPage() {
		return orderListPage;
	}

	public void setOrderListPage(Page orderListPage) {
		this.orderListPage = orderListPage;
	}

	public Page getShopListPage() {
		return shopListPage;
	}

	public void setShopListPage(Page shopListPage) {
		this.shopListPage = shopListPage;
	}

	public void setShopType(int shopType) {
		this.shopType = shopType;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public List<ShopTasteTag> getShopTasteTagList() {
		return shopTasteTagList;
	}

	public void setShopTasteTagList(List<ShopTasteTag> shopTasteTagList) {
		this.shopTasteTagList = shopTasteTagList;
	}

	public String getTagId() {
		return tagId;
	}

	public void setTagId(String tagId) {
		this.tagId = tagId;
	}

	public String getAvatarFileName() {
		return avatarFileName;
	}

	public void setAvatarFileName(String avatarFileName) {
		this.avatarFileName = avatarFileName;
	}
	
	
}
