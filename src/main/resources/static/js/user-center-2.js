/**
 * 获取用户信息
 */
window.page.getUserInfo = function () {
    $.ajax({
        url: '/user/',
        type: 'get',
        dataType:"json",
        async:false,
        success:function (r) {
            if (r.code!=window.FO.ResponseStatus.OK){
                window.FO.postAlert(r);
            }else {
                page.data.user = r.data;
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
window.page.getUserInfo();
/**
 * 获得当前认证用户所有不属于任何仓库的文件
 */
window.page.getFiles = function (warehouseId) {
    $.ajax({
        type: "get",
        async: false,
        url: "/files/list/"+warehouseId,
        success: function (r) {
            if (r.code != FO.ResponseStatus.OK) {
                window.FO.postAlert(r);
            } else {
                page.data.files = r.data;
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
window.page.getFiles(0);
/**
 * 获取 superWarehouseId 指定的父仓库下所有的文件仓库
 */
window.page.getWarehouses = function (superWarehouseId) {
    $.ajax({
        type: "get",
        async: false,
        url: "/warehouses/list/"+superWarehouseId,
        success: function (r) {
            if (r.code != FO.ResponseStatus.OK) {
                window.FO.postAlert(r);
            } else {
                page.data.warehouses = r.data;
            }
        },
        error: function (r) {
            window.FO.postAlert(r);
        }
    });
}
window.page.getWarehouses(window.page.data.user.rootWarehouseId);
window.page.data.warehouseStack.push(window.page.data.user.rootWarehouseId);
/**
 * 获取用户所有的名单
 */
$.ajax({
    type: "get",
    async: false,
    url: "/namelists/list",
    success: function (r) {
        if (r.code != FO.ResponseStatus.OK) {
            window.FO.postAlert(r);
        } else {
            page.data.nameLists = r.data;
        }
    },
    error: function (r) {
        window.FO.postAlert(r);
    }
});


vm = new Vue({
    el:'#sider-content',
    data:page.data,
});

/**
 * 文件列表
 * @type {Vue}
 */
vm2 = new Vue({
    el:"#file-list",
    data:page.data,
    methods:{
        openFileContextMenu:function (e) {

            // 禁用浏览器默认右键上下文菜单
            $(".fo-file").bind("contextmenu", function(){
                event.preventDefault();
                return false;
            });

            $("#item-context-menu").css({
                "top":e.pageY+12,
                "left":e.pageX+12,
                "visibility":"visible"
            });
            //获取当前被单击文件的fileid和filetype,该值保存在该元素的dataset中
            page.data.currentSelectedItemId = $(e.currentTarget).data('fileid');
            page.data.currentSelectedItemType =  $(e.currentTarget).data('filetype');
        },
    }
});

/**
 * 仓库列表
 * @type {Vue}
 */
vm3 = new Vue({
    el:"#warehouse-list",
    data:page.data,
    methods:{
        openWarehouse : function (e) {
            // 不知道为啥这样子不行
            // let warehouseId = $(e.target).parent().data('warehouseid');
            let warehouseId = parseInt(e.target.parentElement.dataset.warehouseid)
            if (warehouseId==undefined) return;
            // 防止用户多次点击
            if (warehouseId==page.data.warehouseStack[page.data.warehouseStack.length-1]){
                return;
            }
            page.getFiles(warehouseId);
            page.getWarehouses(warehouseId);
            page.data.warehouseStack.push(warehouseId);
        },
        closeWarehouse:function (e) {
            page.data.warehouseStack.pop();
            let superWarehouseId = page.data.warehouseStack[page.data.warehouseStack.length-1];
            page.getFiles(superWarehouseId);
            page.getWarehouses(superWarehouseId);
        },
        openFolderContextMenu:function (e){
           //  禁用浏览器默认右键上下文菜单
            $(".fo-folder").bind("contextmenu", function(){
                event.preventDefault();
                return false;
            });

            $("#item-context-menu").css({
                "top":e.pageY+12,
                "left":e.pageX+12,
                "visibility":"visible"
            });
            //获取当前被单击文件的warehouseid和type,该值保存在该元素的dataset中
            page.data.currentSelectedItemId = $(e.currentTarget).data('warehouseid');
            page.data.currentSelectedItemType =  $(e.currentTarget).data('type');
        }
    }
});
/**
 * 右键上下文菜单
 * @type {Vue}
 */
vm4 = new Vue({
    el:"#item-context-menu",
    data:page.data,
    methods:{
        showJoinOrMergeModal : function (e) {
            $('#joinBTN').trigger("click");
        },
        showShareModal : function (e) {
            $("#shareBTN").trigger('click');
        },
        // 显示重命名模态框
        showRenameModal : function (e) {
            $('#showRenameModalBTN').click();
        },
        doClaimFile : function (e) {
            $.ajax({
                url: "/files/claim/"+page.data.currentSelectedItemId,
                type: 'get',
                contentType: "application/json;charset=UTF-8",
                dataType:"json",
                success:function (r) {
                    if (r.code != window.FO.ResponseStatus.OK){
                        window.FO.postAlert(r);
                    }else{
                        Messenger().post({
                            message:"认领成功",
                            hideAfter:1.5
                        });
                        window.page.getFilesNotInWarehouse();
                    }
                },
                error: function () {
                    window.FO.postAlert(r);
                }
            });
        },
        download : function (e) {
            let dest;
            if (page.data.currentSelectedItemType===page.data.TYPE_FILE){
                for (let i=0;i<this.files.length;i++){
                    if (this.files[i].fileId == this.currentSelectedItemId){
                        dest = this.files[i].uuid;
                        break;
                    }
                }
                window.location.href = '/files/download/'+ dest;
            }else if (page.data.currentSelectedItemType==page.data.TYPE_WAREHOUSE){
                for (let i=0;i<this.warehouses.length;i++){
                    if (this.warehouses[i].warehouseId == this.currentSelectedItemId){
                        dest = this.warehouses[i].uuid;
                        break;
                    }
                }
                window.location.href = '/warehouses/download/'+ dest;
            }


        },
        showCollationAndAnalysisModal : function (e) {
            $('#showCollationAndAnalysisModal').click();
        },
        deleteItem : function (e) {
            let postUrl;
            if (page.data.currentSelectedItemType===page.data.TYPE_FILE){
                postUrl = '/files/delete/'+page.data.currentSelectedItemId;
            }else if (page.data.currentSelectedItemType===page.data.TYPE_WAREHOUSE){
                postUrl = '/warehouses/delete/'+page.data.currentSelectedItemId;
            }
            $.ajax({
                url: postUrl,
                type: 'delete',
                contentType: "application/json;charset=UTF-8",
                dataType:"json",
                success:function (r) {
                    if (r.code != window.FO.ResponseStatus.OK){
                        window.FO.postAlert(r);
                    }else{
                        Messenger().post({
                            message:"删除成功",
                            hideAfter:1.5
                        });
                        let stack = window.page.data.warehouseStack;
                        window.page.getWarehouses(stack[stack.length-1]);
                        window.page.getFiles(stack[stack.length-1]);
                    }
                },
                error: function (r) {
                    window.FO.postAlert(r);
                }
            });
        }
    }
});


/**
 * joinOrMerge 模态框
 * @type {Vue}
 */
vm5 = new Vue({
    el:'#joinOrMerge-modal',
    data:page.data,
    methods:{
        joinOrMerge : function () {
            console.log(page.data.currentSelectedItemId);
            let postData,postUrl;
            if (page.data.currentSelectedItemType===page.data.TYPE_FILE){
                postData = {
                    fileId:window.page.data.currentSelectedItemId,
                    warehouseToken:$("input[name='warehouseToken']").val(),
                    newFileName:$("input[name='newFileName']").val(),
                }
                postUrl = '/files/join';
            }else if (page.data.currentSelectedItemType==page.data.TYPE_WAREHOUSE){
               postData = {
                   srcWarehouseId:window.page.data.currentSelectedItemId,
                   destWarehouseToken:$("input[name='warehouseToken']").val(),
               }
                postUrl = '/warehouses/merge';
            }

            $.ajax({
                url: postUrl,
                type: 'patch',
                data: JSON.stringify(postData),
                contentType: "application/json;charset=UTF-8",
                dataType:"json",
                success:function (r) {
                    if (r.code != window.FO.ResponseStatus.OK){
                        window.FO.postAlert(r);
                    }else{
                        Messenger().post({
                            message:"提交或合并成功",
                            hideAfter:1.5
                        });
                        window.page.getFilesNotInWarehouse();
                    }
                },
                error: function (r) {
                    window.FO.postAlert(r);
                }
            });
        }
    }
});

/**
 * share 模态框
 * @type {Vue}
 */
vm6 = new Vue({
    el:'#share-modal',
    data:page.data,
    methods : {
    },
    computed : {
        share1: function () {
            let currentSelectedItemType = this.$data.currentSelectedItemType;
            let currentSelectedItemId = this.$data.currentSelectedItemId;
            if (currentSelectedItemType == this.$data.TYPE_FILE) {
                let files = this.$data.files;
                for (let i = 0; i < files.length; i++) {
                    if (files[i].fileId == currentSelectedItemId) {
                        return files[i].chineseToken;
                    }
                }
            }
            if (currentSelectedItemType == this.$data.TYPE_WAREHOUSE) {
                let warehouses = this.$data.warehouses;
                for (let i = 0; i < warehouses.length; i++) {
                    if (warehouses[i].warehouseId == currentSelectedItemId) {
                        return warehouses[i].chineseToken;
                    }
                }
            }
        },
        share2 : function () {
            let currentSelectedItemType = this.$data.currentSelectedItemType ;
            let currentSelectedItemId = this.$data.currentSelectedItemId ;
            if (currentSelectedItemType == this.$data.TYPE_FILE){
                let files = this.$data.files;
                for(let i=0;i<files.length;i++){
                    if (files[i].fileId == currentSelectedItemId){
                        return files[i].uuid;
                    }
                }
            }
            if (currentSelectedItemType == this.$data.TYPE_WAREHOUSE){
                let warehouses = this.$data.warehouses;
                for(let i=0;i<warehouses.length;i++){
                    if (warehouses[i].warehouseId == currentSelectedItemId){
                        return warehouses[i].uuid;
                    }
                }
            }
        }
    }
});

/**
 * collationAndAnalysis 模态框
 * @type {Vue}
 */
vm7 = new Vue({
    el : "#collationAndAnalysis-modal",
    data : page.data,
    methods : {
        fetchHeader : function (e) {
            let nameListId = e.target.selectedOptions[0].value;
            console.log(nameListId);
            $.ajax({
                url: "/namelists/fetch-header/"+nameListId,
                type: 'get',
                contentType: "application/json;charset=UTF-8",
                dataType:"json",
                success:function (r) {
                    if (r.code != window.FO.ResponseStatus.OK){
                        window.FO.postAlert(r);
                    }
                    window.page.data.nameListHeader = r.data;
                },
                error: function (r) {
                    window.FO.postAlert(r);
                }
            });
        },
        method1 : function (e) {
            let postData = {
                methodKind:1,
                keyColumn : document.getElementsByTagName('select')[1].selectedOptions[0].innerHTML,
                namePattern : document.getElementById('template-input').value,
                warehouseId : page.data.currentSelectedItemId,
                nameListId : parseInt(document.getElementsByTagName('select')[0].selectedOptions[0].value),
            };

            $.ajax({
                url: "/files/collationAndAnalysis",
                data:JSON.stringify(postData),
                type: 'post',
                contentType: "application/json;charset=UTF-8",
                dataType:"json",
                success:function (r) {
                    if (r.code != window.FO.ResponseStatus.OK){
                        window.FO.postAlert(r);
                    }
                    // fixme
                    alert('分析完毕');
                    window.page.data.notFoundList = r.data;
                    $('#analyze-result-modal-btn').click();
                },
                error: function (r) {
                    window.FO.postAlert(r);
                }
            });
        }
    }
});

/**
 * analyze-result 模态框
 * @type {Vue}
 */
vm8 = new Vue({
    el : "#analyze-result-modal",
    data : page.data,
});