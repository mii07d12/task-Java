<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Deleted Tasks</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body class="light-theme"> <!-- 初期テーマを設定 -->
    <div class="container">
        <h1>削除済みタスク</h1>

        <!-- タスク一覧に戻るボタン -->
        <button class="back-button" onclick="location.href='task'">タスク一覧に戻る</button>
        
        <!-- テーマ切り替えボタン -->
        <div>
            <label>
                <input type="radio" name="theme" value="light" checked onchange="switchTheme(this)"> ライトモード
            </label>
            <label>
                <input type="radio" name="theme" value="dark" onchange="switchTheme(this)"> ダークモード
            </label>
        </div>

        <!-- 差し色変更 -->
        <div>
            <label>
                差し色を選択:
                <input type="color" name="accentColor" value="#ADD8E6" onchange="switchAccentColor(this)">
            </label>
        </div>

        <!-- デフォルトに戻すボタン -->
        <button class="default-button" onclick="resetToDefault()">デフォルトに戻す</button>

        <!-- 削除済みタスク表示テーブル -->
        <table class="task-table">
            <thead>
                <tr>
                    <th>タイトル</th>
                    <th>詳細</th>
                    <th>優先度</th>
                    <th>期限</th>
                    <th>カテゴリー</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="task" items="${deletedTaskList}">
                    <tr>
                        <td>${task.title}</td>
                        <td>${task.description}</td>
                        <td>
                            <c:choose>
                                <c:when test="${task.priority == 0}">低</c:when>
                                <c:when test="${task.priority == 1}">中</c:when>
                                <c:when test="${task.priority == 2}">高</c:when>
                            </c:choose>
                        </td>
                        <td>
                            <c:if test="${task.deadline != null}">
                                <fmt:formatDate value="${task.deadlineAsDate}" pattern="yyyy/MM/dd HH:mm" />
                            </c:if>
                        </td>
                        <td>${task.category}</td>
                        <td>
                            <button onclick="location.href='task?action=restore&id=${task.id}'">復元</button>
                            <button onclick="location.href='task?action=permanentDelete&id=${task.id}'">完全削除</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- ポップアップ用のHTML -->
        <div id="taskPopup" class="popup" style="display:none;">
            <div class="popup-content">
                <span class="close" onclick="closePopup()">&times;</span>
                <h2 id="popupTitle"></h2>
                <p id="popupDescription"></p>
                <form id="editForm" action="task" method="post">
                    <input type="hidden" name="id" id="taskId">
                    <input type="text" name="title" id="editTitle" required>
                    <textarea name="description" id="editDescription" required></textarea>
                    <select name="priority" id="editPriority" required>
                        <option value="0">低</option>
                        <option value="1">中</option>
                        <option value="2">高</option>
                    </select>
                    <select name="category" id="editCategory" required>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category}">${category}</option>
                        </c:forEach>
                    </select>
                    <button type="submit">Update Task</button>
                </form>
            </div>
        </div>
    </div>
    
    <script src="./script.js"></script>
    <script src="script.js"></script>
</body>
</html>
