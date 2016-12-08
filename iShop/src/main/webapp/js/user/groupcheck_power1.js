var oc = new ObjectControl();
var groupPower = {
    init: function() { //进页面的时候调用
        this.getPowerlist();
        this.clickWay();
    },
    getSession: function() { //获取本地存储
        var group_corp = JSON.parse(sessionStorage.getItem("group_corp")); //取本地的群组编号
        return group_corp
    },
    pageRendering: function(list) { //绘制页面
        console.log(list);
        var tr = "";
        for (var i = 0; i < list.length; i++) {
            var num = i + 1;
            tr += "<tr data-function='" + list[i].function_code + "'><td style='text-align: left;padding-left:22px;width:7.21%;'>" + num + "</td><td style='width:12.78%;'>" + list[i].module_name + "</td><td style='width: 12.78%;'>" + list[i].function_name +
                "</td><td style='width: 22.69%;'><div class='action_name'><ul>"
            for (var j = 0; j < list[i].actions.length; j++) {
                var color = "";
                if (list[i].actions[j].is_die == "Y") {
                    color = "die";
                }
                if (list[i].actions[j].is_die == "N" && list[i].actions[j].is_live == "N") {
                    color = "";
                }
                if (list[i].actions[j].is_die == "N" && list[i].actions[j].is_live == "Y") {
                    color = "active";
                }
                tr += "<li class='" + color + "' data-actionCode='" + list[i].actions[j].action_code + "' data-actionName='" + list[i].actions[j].action_name +
                    "' data-actionId='" + list[i].actions[j].action_id + "'>" +
                    list[i].actions[j].show_name + "</li>"
            }
            tr += "</ul></div></td><td style='width: 44.54%;'><div class='modify_options'><ul>";
            for (var k = 0; k < list[i].columns.length; k++) {
                var color = "";
                if (list[i].columns[k].is_die == "Y") {
                    color = "die";
                }
                if (list[i].columns[k].is_die == "N" && list[i].columns[k].is_live == "N") {
                    color = "";
                }
                if (list[i].columns[k].is_die == "N" && list[i].columns[k].is_live == "Y") {
                    color = "active";
                }
                tr += "<li class='" + color + "' data-columnId='" + list[i].columns[k].column_id + "' data-columnName='" + list[i].columns[k].column_name + "'>" +
                    list[i].columns[k].show_name + "</li>"
            }
            tr += "</ul></div></td></tr>";
        }
        $("#table tbody").html(tr);
    },
    getPowerlist: function() {
        var self = this;
        var group_corp = self.getSession();
        var param = {};
        param["corp_code"] = group_corp.corp_code;
        param["group_code"] = group_corp.group_code;
        oc.postRequire("post", "/user/group/check_power1", "0", param, function(data) {
            var message = JSON.parse(data.message);
            var list = message.list;
            self.pageRendering(list);
        });
    },
    clickWay: function() {
        var self = this;
        $("#turnoff").bind("click", function() {
            $(window.parent.document).find('#iframepage').attr("src", "/user/group_edit.html");
        });
        $(".power_table").on("click", "ul li", function() {
            var class_name = $(this).attr("class");
            if (class_name == "die") {
                return;
            }
            if (class_name !== "die") {
                $(this).toggleClass("active");
            }
        });
        $("#save").click(function() { //点击保存按钮获取列表内容
            var tr = $("#table tbody tr");
            var group_corp = self.getSession();
            var param = {};
            var add_action = []; //动作
            var add_column = []; //列表项
            var del_act_id = ""; //取消时获取的动作id
            var del_col_id = ""; //取消时获取的修改项id
            param["corp_code"] = group_corp.corp_code;
            param["group_code"] = group_corp.group_code;
            for (var i = 0; i < tr.length; i++) {
                var function_code = $(tr[i]).attr("data-function");
                var action_li = $(tr[i]).find(".action_name ul li.active"); //动作的选中项
                var column_li = $(tr[i]).find(".modify_options ul li.active"); //修改项的选中项
                var action_id_li = $(tr[i]).find(".action_name ul li"); //动作多有的项
                var column_id_li = $(tr[i]).find(".modify_options ul li"); //允许修改项的所有项
                for (var j = 0; j < action_li.length; j++) {
                    var action_code = $(action_li[j]).attr("data-actioncode");
                    var action_codes = {
                        "action_code": action_code,
                        "function_code": function_code
                    };
                    add_action.push(action_codes);
                };
                for (var k = 0; k < column_li.length; k++) {
                    var column_name = $(column_li[k]).attr("data-columnname");
                    var column_names = {
                        "column_name": column_name,
                        "function_code": function_code
                    };
                    add_column.push(column_names);
                };
                for (var l = action_id_li.length - 1; l >= 0; l--) {
                    var class_name = $(action_id_li[l]).attr("class");
                    if (class_name !== "die") {
                        var action_id = $(action_id_li[l]).attr("data-actionid");
                        if (action_id !== "" && action_id !== undefined) {
                            if (l > 0) {
                                del_act_id += action_id + ",";
                            } else {
                                del_act_id += action_id;
                            }
                        }
                    }    
                };
                for (var m = column_id_li.length; m >= 0; m--) {
                    var column_id = $(column_id_li[m]).attr("data-columnid");
                    if (column_id !== "" && column_id !== undefined) {
                        if (m > 0) {
                            del_col_id += column_id + ",";
                        } else {
                            del_col_id += column_id;
                        }
                    }
                }
            }
            param["add_action"] = add_action;
            param["add_column"] = add_column;
            param["del_act_id"] = del_act_id;
            param["del_col_id"] = del_col_id;
            oc.postRequire("post","/user/group/check_power/save1","0",param,function(data){
                console.log(data);
            })
        })
    }
};
$(function() {
    groupPower.init();
})