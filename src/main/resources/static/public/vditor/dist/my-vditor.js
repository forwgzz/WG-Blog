const cdn = "/public/vditor";
const emojiPath = "/public/emoji";
const customEmoji = {
    "e-guoqi": emojiPath + "/guoqi.png",
    "e-doge": emojiPath + "/doge.png",
    "e-huaji": emojiPath + "/huaji.gif",
    "e-trollface": emojiPath + "/trollface.png",
    "e-wulian": emojiPath + "/wulian.png",
    "e-octocat": emojiPath + "/octocat.png"
}

var hljs = {
    lineNumber: true,//行号
    style: "a11y-dark"//代码块主题
}

function clickEditor(ev) {
    const el = ev.target
    const el_outline_item = this.findThisOrAncestor(el, '[data-target-id]')
    // warn('el_outline_item', el_outline_item)
    if (el_outline_item) {
        const outline_item_target_id = el_outline_item.getAttribute('data-target-id')
        const el_target = document.getElementById(outline_item_target_id)
        el_target.scrollIntoView()
    }
}

function findThisOrAncestor(el, selector) {
    while (el) {
        if (el.matches(selector)) {
            return el
        } else {
            el = el.parentElement
        }
    }
    return null
}