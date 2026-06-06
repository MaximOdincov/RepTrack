package com.example.reptrack.presentation.auth.dialogs

object PrivacyPolicyText {
    const val RU_TITLE = "Политика конфиденциальности"
    const val EN_TITLE = "Privacy Policy"

    const val RU_EFFECTIVE_DATE = "Дата вступления в силу: 6 Мая 2026 г."
    const val EN_EFFECTIVE_DATE = "Effective date: May 6, 2026."

    // Russian version
    val RU_CONTENT = """
1. Общие положения
Настоящая Политика конфиденциальности регулирует порядок обработки и защиты персональных данных пользователей при использовании мобильного приложения RepTrack (далее – "Приложение").

2. Сбор и использование персональных данных
2.1. Данные, которые мы собираем
Мы собираем только те данные, которые необходимы для функционирования Приложения и предоставления услуг:
- Имя пользователя: Для идентификации вас в системе
- Адрес электронной почты: Для восстановления пароля и связи с вами
- Данные о тренировках: Для отслеживания вашего прогресса
- Данные о весе: Для построения графиков статистики
- Данные о группах мышц: Для анализа тренировочной нагрузки

2.2. Использование данных
Ваши данные используются исключительно для:
- Предоставления доступа к вашим тренировочным записям
- Анализа вашего прогресса и статистики
- Предоставления персонализированных рекомендаций
- Уведомлений о важных обновлениях Приложения

3. Хранение данных
3.1. Место хранения
Ваши данные хранятся на серверах Firebase Google, которые соответствуют международным стандартам безопасности.

3.2. Срок хранения
Ваши персональные данные хранятся до момента вашего добровольного удаления аккаунта или удаления данных Приложения.

4. Защита данных
Мы принимаем следующие меры для защиты ваших данных:
- Шифрование данных при передаче (HTTPS/TLS)
- Шифрование данных при хранении
- Регулярное резервное копирование данных
- Ограниченный доступ к персональным данным
- Регулярный аудит безопасности

5. Совместное использование данных
Мы не передаем ваши данные третьим лицам, за исключением:
- Партнерам, предоставляющим техническую поддержку Приложения
- Государственным органам в соответствии с законодательством
- При продаже или передаче бизнеса (с вашего согласия)

6. Ваши права
Как пользователь Приложения, вы имеете право:
- Получить доступ к вашим персональным данным
- Требовать исправления неточных данных
- Требовать удаления ваших данных
- Возражать против обработки ваших данных
- Отозвать согласие на обработку данных

7. Cookies и аналитика
Приложение может использовать файлы cookies для:
- Запоминания вашего входа в систему
- Анализа использования Приложения
- Улучшения пользовательского интерфейса

8. Дети и несовершеннолетние
Приложение не предназначено для лиц младше 13 лет. Мы сознательно не собираем данные детей.

9. Изменения в политике
Мы можем изменять настоящую Политику конфиденциальности. При изменении, мы:
- Уведомим вас через Приложение
- Разместим новую версию Политики в Приложении
- Предоставим возможность ознакомиться с изменениями

10. Связь с нами
Если у вас есть вопросы по Политике конфиденциальности, пожалуйста, свяжитесь с нами:
- Email: support@reptrack.app
- Через форму обратной связи в Приложении

11. Последствия принятия Политики
Принимая настоящую Политику конфиденциальности, вы подтверждаете:
- Что ознакомились с содержанием Политики
- Что согласны с условиями обработки ваших данных
- Что понимаете риски, связанные с использованием Приложения
- Что согласны на сбор и использование указанных выше данных
    """.trimIndent()

    // English version
    val EN_CONTENT = """
1. General Provisions
This Privacy Policy governs the processing and protection of user personal data when using the RepTrack mobile application (hereinafter referred to as the "Application").

2. Collection and Use of Personal Data
2.1. Data We Collect
We collect only the data necessary for the Application to function and provide services:
- Username: For identification in the system
- Email address: For password recovery and communication with you
- Workout data: For tracking your progress
- Weight data: For building statistics charts
- Muscle group data: For analyzing workout load

2.2. Data Usage
Your data is used exclusively for:
- Providing access to your workout records
- Analyzing your progress and statistics
- Providing personalized recommendations
- Notifying you about important Application updates

3. Data Storage
3.1. Storage Location
Your data is stored on Google Firebase servers, which comply with international security standards.

3.2. Storage Period
Your personal data is stored until you voluntarily delete your account or delete the Application data.

4. Data Protection
We take the following measures to protect your data:
- Data encryption during transmission (HTTPS/TLS)
- Data encryption during storage
- Regular data backup
- Limited access to personal data
- Regular security audit

5. Data Sharing
We do not transfer your data to third parties, except for:
- Partners providing technical support for the Application
- Government agencies in accordance with the law
- When selling or transferring the business (with your consent)

6. Your Rights
As an Application user, you have the right to:
- Access your personal data
- Request correction of inaccurate data
- Request deletion of your data
- Object to processing your data
- Withdraw consent for data processing

7. Cookies and Analytics
The Application may use cookies for:
- Remembering your login
- Analyzing Application usage
- Improving the user interface

8. Children and Minors
The Application is not intended for users under 13 years of age. We consciously do not collect data from children.

9. Changes to Policy
We may modify this Privacy Policy. When making changes, we will:
- Notify you through the Application
- Post the new version of the Policy in the Application
- Provide an opportunity to review the changes

10. Contact Us
If you have questions about the Privacy Policy, please contact us:
- Email: support@reptrack.app
- Through the feedback form in the Application

11. Consequences of Accepting the Policy
By accepting this Privacy Policy, you confirm that:
- You have read the content of the Policy
- You agree to the terms of processing your data
- You understand the risks associated with using the Application
- You agree to the collection and use of the above data
    """.trimIndent()
}