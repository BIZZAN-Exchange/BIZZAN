//除法函数，用来得到精确的除法结果
//说明：javascript的除法结果会有误差，在两个浮点数相除的时候会比较明显。这个函数返回较为精确的除法结果。
//调用：accDiv(arg1,arg2)
//返回值：arg1除以arg2的精确结果
function accDiv(arg1, arg2) {
    var t1 = 0, t2 = 0, r1, r2;
    try { t1 = arg1.toString().split(".")[1].length } catch (e) { }
    try { t2 = arg2.toString().split(".")[1].length } catch (e) { }
    // with (Math) {
    //     r1 = Number(arg1.toString().replace(".", ""))
    //     r2 = Number(arg2.toString().replace(".", ""))
    //     return (r1 / r2) * pow(10, t2 - t1);
    // }
    r1 = Number(arg1.toString().replace(".", ""))
    r2 = Number(arg2.toString().replace(".", ""))
    return (r1 / r2) * Math.pow(10, t2 - t1);
}

//给Number类型增加一个div方法，调用起来更加 方便。
Number.prototype.div = function (arg) {
    return accDiv(this, arg);
}
String.prototype.div = function (arg) {
    return accDiv(this, arg);
}

//乘法函数，用来得到精确的乘法结果
//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。
//调用：accMul(arg1,arg2)
//返回值：arg1乘以 arg2的精确结果
function accMul(arg1, arg2) {
    var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
    try { m += s1.split(".")[1].length } catch (e) { }
    try { m += s2.split(".")[1].length } catch (e) { }
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m)
}

// 给Number类型增加一个mul方法，调用起来更加方便。
Number.prototype.mul = function (arg) {
    return accMul(arg, this);
}
String.prototype.mul = function (arg) {
    return accMul(arg, this);
}

export function parseTime(time, cFormat, xArr, aArr) {

    if ((time + "").length === 10) {
        time = +time * 1000;
    }

    xArr = xArr && xArr.length === 2 ? xArr : ['AM', 'PM']
    aArr = aArr && aArr.length === 7 ? aArr : ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]

    const format = cFormat || "{y}-{m}-{d} {h}:{i}:{s}";
    let date;
    if (typeof time === "object") {
        date = time;
    } else if (!isNaN(parseInt(time))){
        date = new Date(parseInt(time));
    } else {
        date = new Date(time);
    }
    const formatObj = {
        y: date.getFullYear(),
        m: date.getMonth() + 1,
        d: date.getDate(),
        h: date.getHours(),
        i: date.getMinutes(),
        s: date.getSeconds(),
        a: date.getDay()
    };
    const timeStr = format.replace(/{(y|m|d|h|i|s|a|x)+}/g, (result, key) => {
        let value = formatObj[key];
        if (key === "x") {
            let h = formatObj['h']
            return h <= 12 ? xArr[0] : xArr[1]
        }
        if (key === "a")
            return aArr[value - 1];
        if (result.length > 0 && value < 10) {
            value = "0" + value;
        }
        return value || 0;
    });
    return timeStr;
}

/**
 * @description 隐藏邮箱号
 * @param {*} email
 * @returns email
 */
export function hideEmailInfo (email) {
    if (String (email).indexOf ('@') > 0) {
        let newEmail, str = email.split('@'), _s = '';

        if (str[0].length > 4) {
            _s = str[0].substr (0, 4);
            for (let i = 0; i < str[0].length - 4; i++) {
                _s += '*';
            }
        } else {
            _s = str[0].substr (0, 1);
            for (let i = 0; i < str[0].length - 1; i++) {
                _s += '*';
            }
        }
        newEmail = _s + '@' + str[1];
        return newEmail;
    } else {
        return email;
    }
};

/**
 * @description 隐藏手机号中间4位数
 * @param {*} mobile
 * @returns mobile
 */
export function hideMobileInfo (mobile) {
    let newMobile = '';
    if (mobile.length > 7) {
        newMobile=mobile.substr(0, 3) + '****' + mobile.substr(7);
        return newMobile;
    } else {
        return mobile;
    }
}


export default {
    accDiv,
    accMul,
    parseTime,
    hideEmailInfo,
    hideMobileInfo
}