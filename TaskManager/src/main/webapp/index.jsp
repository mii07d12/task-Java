<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<html>
<head>
    <title>Task Manager</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body class="light-theme"> <!-- 初期テーマを設定 -->
    <div class="container">
        <h1>タスク管理</h1>
        
        <!-- テーマと差し色切り替え -->
        <div class="horizontally-arranged">
            <!-- テーマ切り替えボタン -->
            <div class="flex-item">
                <label>
                    <input type="radio" name="theme" value="light" checked onchange="switchTheme(this)"> ライトモード
                </label>
                <label>
                    <input type="radio" name="theme" value="dark" onchange="switchTheme(this)"> ダークモード
                </label>
            </div>

            <!-- 差し色変更 -->
            <div class="flex-item">
                <label>
                    差し色を選択:
                    <input type="color" name="accentColor" value="#ADD8E6" onchange="switchAccentColor(this)">
                </label>
            </div>

            <!-- デフォルトに戻すボタン -->
            <button class="default-button flex-item" onclick="resetToDefault()">デフォルトに戻す</button>
        </div>

        <!-- カテゴリ追加フォームと検索フォーム -->
        <div class="horizontally-arranged">
            <!-- カテゴリ追加フォーム -->
            <form action="task" method="post" class="category-form flex-item">
                <input type="hidden" name="action" value="addCategory">
                <label for="newCategory">新しいカテゴリ:</label>
                <input type="text" id="newCategory" name="newCategory" required>
                <button type="submit">追加</button>
            </form>

            <!-- 検索フォーム -->
            <form action="task" method="get" class="search-form flex-item">
                <input type="text" name="keyword" placeholder="検索" required>
                <button type="submit">検索</button>
                <input type="hidden" name="action" value="search">
            </form>
        </div>
   
        <!-- タスク追加フォーム -->
        <form action="task" method="post" id="taskForm">
            <label for="title">タイトル: <small>(例: 完了タスクの確認)</small></label>
            <input type="text" id="title" name="title" required>
        
            <label for="description">説明: <small>(例: タスクの詳細を記述)</small></label>
            <textarea id="description" name="description" required></textarea>
        
            <div class="flex-container">
                <div class="flex-item">
                    <label for="priority">優先度 <small>(例: 低, 中, 高)</small></label>
                    <select id="priority" name="priority" required>
                        <option value="0">低</option>
                        <option value="1">中</option>
                        <option value="2">高</option>
                    </select>
                </div>
                <div class="flex-item">
                    <label for="deadline">締切 <small></small></label>
                    <input type="datetime-local" id="deadline" name="deadline">
                </div>
                <div class="flex-item">
                    <label for="category">カテゴリ <small></small></label>
                    <select id="category" name="category" required>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category}">${category}</option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        
            <button type="submit">タスクを追加</button>
            <button type="button" onclick="clearForm()">クリア</button>
        </form>

        <h2>タスクリスト</h2>
        
        <table class="task-table">
            <thead>
                <tr>
                    <th>タイトル</th>
                    <th></th>
                    <th>優先度</th>
                    <th>期限</th>
                    <th>カテゴリー</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="task" items="${taskList}">
                    <tr id="task-row-${task.id}" class="${task.completed ? 'grayed-out' : ''}">
                        <td>${task.title}</td>
                        <td>
                            <button onclick="showDetails('${task.id}', '${task.title}', '${task.description}', ${task.priority}, '${task.category}', '${task.deadline != null ? task.deadline : ''}')">詳細</button>
                        </td>
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
                            <button onclick="toggleCompletion('${task.id}', ${task.completed})">${task.completed ? '未完了にする' : '完了にする'}</button>
                            <button onclick="deleteTask('${task.id}')">削除</button>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <!-- 削除済みタスク一覧に遷移するボタン -->
        <br><button onclick="location.href='task?action=deletedTasks'">削除済みタスクを表示</button>
    </div>

    <!-- ポップアップ用のHTML -->
    <div id="taskPopup" class="popup" style="display:none;">
        <div class="popup-content">
            <span class="close" onclick="closePopup()">&times;</span>
            <h2 id="popupTitle"></h2>
            <p id="popupDescription"></p>
            <form id="editForm" action="task" method="post">
                <input type="hidden" name="id" id="taskId">
                <label for="editTitle">タイトル:</label>
                <input type="text" name="title" id="editTitle" required>
                
                <label for="editDescription">説明:</label>
                <textarea name="description" id="editDescription" required></textarea>
                
                <label for="editPriority">優先度:</label>
                <select name="priority" id="editPriority" required>
                    <option value="0">低</option>
                    <option value="1">中</option>
                    <option value="2">高</option>
                </select>
                
                <label for="editDeadline">締切:</label>
                <input type="datetime-local" name="deadline" id="editDeadline">
                
                <label for="editCategory">カテゴリ:</label>
                <select name="category" id="editCategory" required>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category}">${category}</option>
                    </c:forEach>
                </select>
                
                <button type="submit">編集</button>
            </form>
        </div>
    </div>

    <script src="./script.js"></script>
    <script src="script.js"></script>
</body>
</html>
