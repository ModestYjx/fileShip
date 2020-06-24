

/**
 * 复制到粘贴板
 */
window.clipboard = new ClipboardJS('.fo-copy-btn',{
    container: document.getElementById('share-modal')
});

page.renameItem = function(e) {
    let postData;
    let postUrl;

    if (window.page.data.currentSelectedItemType == window.page.data.TYPE_FILE){
        postData = {
            fileId : page.data.currentSelectedItemId,
                newName : $('#rename-input').val()
        }
        postUrl = '/files/rename';
    }
    else if (window.page.data.currentSelectedItemType == window.page.data.TYPE_WAREHOUSE){
        postData =  {
            warehouseId : page.data.currentSelectedItemId,
            newName : $('#rename-input').val()
        }
        postUrl = '/warehouses/rename';
    }
    $.ajax({
        url: postUrl,
        type: 'patch',
        data: JSON.stringify(postData),
        contentType: "application/json;charset=UTF-8",
        dataType: "json",
        success: function (r) {
            if (r.code != window.FO.ResponseStatus.OK) {
                window.FO.postAlert(r);
            } else {
                Messenger().post({
                    message: "重命名成功",
                    hideAfter: 1.5
                });
                window.page.getFilesNotInWarehouse();
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
/**
 * 禁用浏览器默认右键上下文菜单
 */
$(".fo-folder").bind("contextmenu", function(){
    event.preventDefault();
    return false;
});

$(".fo-file").bind("contextmenu", function(){
    event.preventDefault();
    return false;
});
/**
 * 侧边栏
 * 1. 当侧边栏缩回时设置按钮为右箭头
 * 2. 当侧边栏弹出时设置按钮为左箭头
 */
$(function(){
    $('#sider-menu').sidr({
        name:'sider-content',
        body:'main',
        onOpenEnd:function () {
            $("#sider-menu>span").removeClass("glyphicon glyphicon-chevron-right");
            $("#sider-menu>span").addClass("glyphicon glyphicon-chevron-left");
        },
        onCloseEnd:function () {
            $("#sider-menu>span").removeClass("glyphicon glyphicon-chevron-left");
            $("#sider-menu>span").addClass("glyphicon glyphicon-chevron-right");
        }
    });
    $.sidr('open', 'sider-content');
});

/**
 * 为侧边栏绑定手指滑动事件
 */
$('main').swipe( {
    swipeLeft: function () {
        $.sidr('close', 'sider-content');
    },
    swipeRight: function () {
        $.sidr('open', 'sider-content');
    },
    threshold: 45
});

/**
 * 文件夹左键事件
 * 1. 隐藏folder-list
 * 2. 显示file-list
 * 3. 设置返回图标的大小
 */
$(".fo-file").click(function () {
    $(".fo-file-list").hide();
    $(".fo-file-list").show();
    var s = $(".fo-file").css("width");
    $(".glyphicon-arrow-left").css("fontSize",s);

    // 初始化文件列表
    page.files  = document.getElementsByClassName("fo-file");
    for(let i=0;i<page.files.length;i++){
        page.files[i].ondragstart = function () {
            page.draggingItem = this;
            $("#fullTrashCan").removeClass("fo-fullTrashCanRemoved");
            $("#fullTrashCan").hide();
            $("#emptyTrashCan").show();
        }
        page.files[i].ondragend = function () {
            $("#emptyTrashCan").hide();
        }
    }
});

/**
 * 返回箭头左键事件
 * 1. 隐藏file-list
 * 2. 显示folder-list
 */
$(".glyphicon-arrow-left").click(function () {
    $(".fo-file-list").hide();
    $(".fo-file-list").show();
});

/**
 * 拖拽文件夹到垃圾桶删除
 * @type {Element}
 */
page.emptyTrashCan = document.getElementById("emptyTrashCan");//空垃圾桶
page.folders = document.getElementsByClassName("fo-file");//所有的文件夹
page.draggingItem = null; //正在被拖取的文件夹
// 当文件夹被拖取时指定事件
for(let i=0;i<page.folders.length;i++){
    page.folders[i].ondragstart = function () {
        page.draggingItem = this;
        $("#fullTrashCan").removeClass("fo-fullTrashCanRemoved");
        $("#fullTrashCan").hide();
        $("#emptyTrashCan").show();
    }
    page.folders[i].ondragend = function () {
        $("#emptyTrashCan").hide();
    }
}
/**
 * 当被拖动元素离开目标元素时
 */
page.emptyTrashCan.ondragleave = function(){
    var c = confirm("确认要删除该文件吗?");
    if(c){
        page.draggingItem.parentNode.remove();
        $("#emptyTrashCan").hide();
        $("#fullTrashCan").show();
        $("#fullTrashCan").addClass("fo-fullTrashCanRemoved");
        $("#fullTrashCan").css("left","0px");
        page.draggingItem.remove();
        $("#fullTrashCan").animate({
            left:'-300px'
        },"slow");
    }
};

/**
 * 点击导航栏的搜索按钮,将匹配的文件夹标红
 */
$("#searchFolderBTN").click(function () {
    var index = document.getElementById("searchFolderIndex").value;
    var items = document.getElementsByClassName("fo-item-name");
    // 先把之前的标红的去掉背景
    for(let i=0;i<items.length;i++){
        let item = items[i];

        if (item.style.backgroundColor==="yellow"){
            item.style.backgroundColor = "white";
            //$(child).css("background-color","yellow");
        }
    }
    // 遍历查找名字含有index的文件夹/文件
    for(let i=0;i<items.length;i++){
        let item = items[i];

        if (item.innerHTML.indexOf(index)!=-1){
            item.style.backgroundColor = "yellow";
           //$(child).css("background-color","yellow");
        }
    }
});


/**
 * 创建仓库
 */
page.createWarehouse = function () {
    let superWarehouseId = page.data.warehouseStack[page.data.warehouseStack.length-1]
    $.ajax({
        url: "/warehouses/create/"+superWarehouseId,
        type: 'post',
        data:JSON.stringify( {
            warehouseName:$('input[name="warehouse-name"]').val(),
            constraint:$("input[name='constraint']").val()
        }),
        contentType: "application/json;charset=UTF-8",
        dataType:"json",
        success:function (r) {
            if (r.code!=window.FO.ResponseStatus.OK){
                alert(r.msg);
            }else{
                Messenger().post({
                    message:"文件仓库创建成功！",
                    hideAfter:1.5
                });
                window.page.getWarehouses(superWarehouseId);
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
// 点击其他地方时，隐藏掉这个右键菜单
$(this).click(function(){
    $(".fo-right-hand-panel").css("visibility","hidden");
});


