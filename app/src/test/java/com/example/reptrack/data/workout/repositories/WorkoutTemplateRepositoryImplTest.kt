package com.example.reptrack.data.workout.repositories

import com.example.reptrack.data.local.aggregates.WorkoutTemplateWithExercises
import com.example.reptrack.data.local.dao.WorkoutTemplateDao
import com.example.reptrack.data.local.models.ExerciseDb
import com.example.reptrack.data.local.models.WorkoutTemplateDb
import com.example.reptrack.domain.workout.MuscleGroup
import com.example.reptrack.domain.workout.TemplateSchedule
import com.example.reptrack.domain.workout.WorkoutTemplate
import com.example.reptrack.domain.workout.ExerciseType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class WorkoutTemplateRepositoryImplTest {

    private lateinit var repository: WorkoutTemplateRepositoryImpl
    private val templateDao: WorkoutTemplateDao = mockk()

    @Before
    fun setUp() {
        repository = WorkoutTemplateRepositoryImpl(templateDao)
    }

    // ============ observeTemplateById Tests ============

    @Test
    fun `observeTemplateById returns template when exists`() = runTest {
        // Arrange
        val templateId = "template123"
        val templateDb = createMockTemplateDb(templateId, "Push Day")
        val exercises = listOf(createMockExerciseDb("ex1"), createMockExerciseDb("ex2"))
        val aggregate = WorkoutTemplateWithExercises(
            template = templateDb,
            exercises = exercises
        )

        coEvery { templateDao.observeTemplateById(templateId) } returns flowOf(aggregate)

        // Act
        val result = repository.observeTemplateById(templateId)

        // Assert
        val template = result.first()
        assertNotNull(template)
        assertEquals(templateId, template.id)
        assertEquals("Push Day", template.name)
        assertEquals(2, template.exerciseIds.size)
        assertEquals("ex1", template.exerciseIds[0])
        assertEquals("ex2", template.exerciseIds[1])
    }

    @Test
    fun `observeTemplateById returns null when not found`() = runTest {
        // Arrange
        val templateId = "nonexistent"
        coEvery { templateDao.observeTemplateById(templateId) } returns flowOf(null)

        // Act
        val result = repository.observeTemplateById(templateId)

        // Assert
        val template = result.first()
        assertNull(template)
    }

    @Test
    fun `observeTemplateById with schedule parses schedule correctly`() = runTest {
        // Arrange
        val templateId = "template123"
        val week1Days = setOf(0, 2, 4) // Mon, Wed, Fri
        val week2Days = setOf(1, 3, 5) // Tue, Thu, Sat
        val templateDb = createMockTemplateDb(
            id = templateId,
            name = "Rotating Workout",
            week1Days = week1Days,
            week2Days = week2Days
        )
        val aggregate = WorkoutTemplateWithExercises(
            template = templateDb,
            exercises = emptyList()
        )

        coEvery { templateDao.observeTemplateById(templateId) } returns flowOf(aggregate)

        // Act
        val result = repository.observeTemplateById(templateId)

        // Assert
        val template = result.first()
        assertNotNull(template)
        assertNotNull(template.schedule)
        assertEquals(week1Days, template.schedule!!.week1Days)
        assertEquals(week2Days, template.schedule!!.week2Days)
    }

    // ============ observeAllTemplates Tests ============

    @Test
    fun `observeAllTemplates returns all templates`() = runTest {
        // Arrange
        val template1 = createMockTemplateDb("t1", "Push")
        val template2 = createMockTemplateDb("t2", "Pull")
        val template3 = createMockTemplateDb("t3", "Legs")

        val aggregates = listOf(
            WorkoutTemplateWithExercises(template1, listOf(createMockExerciseDb("ex1"))),
            WorkoutTemplateWithExercises(template2, listOf(createMockExerciseDb("ex2"))),
            WorkoutTemplateWithExercises(template3, emptyList())
        )

        coEvery { templateDao.observeTemplates() } returns flowOf(aggregates)

        // Act
        val result = repository.observeAllTemplates()

        // Assert
        val templates = result.first()
        assertEquals(3, templates.size)
        assertEquals("Push", templates[0].name)
        assertEquals("Pull", templates[1].name)
        assertEquals("Legs", templates[2].name)
    }

    @Test
    fun `observeAllTemplates returns empty list when no templates`() = runTest {
        // Arrange
        coEvery { templateDao.observeTemplates() } returns flowOf(emptyList())

        // Act
        val result = repository.observeAllTemplates()

        // Assert
        val templates = result.first()
        assertTrue(templates.isEmpty())
    }

    // ============ observeTemplatesByDayOfWeek Tests ============

    @Test
    fun `observeTemplatesByDayOfWeek filters for week1 day`() = runTest {
        // Arrange
        val week1Days = setOf(0, 2, 4) // Mon, Wed, Fri
        val week2Days = setOf(1, 3)    // Tue, Thu

        val matchingTemplate = createMockTemplateDb(
            "t1",
            "Push Day",
            week1Days = week1Days,
            week2Days = week2Days
        )
        val otherTemplate = createMockTemplateDb("t2", "Other", null, null)

        val aggregates = listOf(
            WorkoutTemplateWithExercises(matchingTemplate, emptyList()),
            WorkoutTemplateWithExercises(otherTemplate, emptyList())
        )

        coEvery { templateDao.observeTemplates() } returns flowOf(aggregates)

        // Act - Monday (0) of first week
        val result = repository.observeTemplatesByDayOfWeek(0, isSecondWeek = false)

        // Assert
        val templates = result.first()
        assertEquals(1, templates.size)
        assertEquals("Push Day", templates[0].name)
    }

    @Test
    fun `observeTemplatesByDayOfWeek filters for week2 day`() = runTest {
        // Arrange
        val week1Days = setOf(0, 2)     // Mon, Wed
        val week2Days = setOf(1, 3, 5)  // Tue, Thu, Sat

        val matchingTemplate = createMockTemplateDb(
            "t1",
            "Pull Day",
            week1Days = week1Days,
            week2Days = week2Days
        )
        val otherTemplate = createMockTemplateDb("t2", "Other", null, null)

        val aggregates = listOf(
            WorkoutTemplateWithExercises(matchingTemplate, emptyList()),
            WorkoutTemplateWithExercises(otherTemplate, emptyList())
        )

        coEvery { templateDao.observeTemplates() } returns flowOf(aggregates)

        // Act - Tuesday (1) of second week
        val result = repository.observeTemplatesByDayOfWeek(1, isSecondWeek = true)

        // Assert
        val templates = result.first()
        assertEquals(1, templates.size)
        assertEquals("Pull Day", templates[0].name)
    }

    @Test
    fun `observeTemplatesByDayOfWeek returns empty when no match`() = runTest {
        // Arrange
        val template = createMockTemplateDb("t1", "Workout", null, null)
        val aggregates = listOf(
            WorkoutTemplateWithExercises(template, emptyList())
        )

        coEvery { templateDao.observeTemplates() } returns flowOf(aggregates)

        // Act
        val result = repository.observeTemplatesByDayOfWeek(0, isSecondWeek = false)

        // Assert
        val templates = result.first()
        assertTrue(templates.isEmpty())
    }

    // ============ createTemplate Tests ============

    @Test
    fun `createTemplate successfully creates template with exercises`() = runTest {
        // Arrange
        val template = WorkoutTemplate(
            id = "template123",
            name = "Full Body",
            iconId = "icon_123",
            exerciseIds = listOf("ex1", "ex2", "ex3"),
            schedule = TemplateSchedule(
                week1Days = setOf(0, 2, 4),
                week2Days = setOf(1, 3, 5)
            )
        )

        coEvery { templateDao.insertFullTemplate(any(), any()) } returns Unit

        // Act
        val result = repository.createTemplate(template)

        // Assert
        assertTrue(result.isSuccess)
        coVerify {
            templateDao.insertFullTemplate(
                template = any(),
                exerciseIds = listOf("ex1", "ex2", "ex3")
            )
        }
    }

    @Test
    fun `createTemplate with no schedule`() = runTest {
        // Arrange
        val template = WorkoutTemplate(
            id = "template123",
            name = "Custom",
            iconId = null,
            exerciseIds = listOf("ex1"),
            schedule = null
        )

        coEvery { templateDao.insertFullTemplate(any(), any()) } returns Unit

        // Act
        val result = repository.createTemplate(template)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { templateDao.insertFullTemplate(any(), listOf("ex1")) }
    }

    @Test
    fun `createTemplate propagates exception on failure`() = runTest {
        // Arrange
        val template = createMockWorkoutTemplate("template123")
        val exception = RuntimeException("Database error")
        coEvery { templateDao.insertFullTemplate(any(), any()) } throws exception

        // Act
        val result = repository.createTemplate(template)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // ============ updateTemplate Tests ============

    @Test
    fun `updateTemplate successfully updates template`() = runTest {
        // Arrange
        val template = WorkoutTemplate(
            id = "template123",
            name = "Updated Name",
            iconId = "icon_456",
            exerciseIds = listOf("ex1", "ex2"),
            schedule = null
        )

        coEvery { templateDao.deleteTemplateExercises(any()) } returns Unit
        coEvery { templateDao.insertFullTemplate(any(), any()) } returns Unit

        // Act
        val result = repository.updateTemplate(template)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { templateDao.deleteTemplateExercises("template123") }
        coVerify {
            templateDao.insertFullTemplate(
                template = any(),
                exerciseIds = listOf("ex1", "ex2")
            )
        }
    }

    @Test
    fun `updateTemplate propagates exception on failure`() = runTest {
        // Arrange
        val template = createMockWorkoutTemplate("template123")
        val exception = RuntimeException("Update failed")
        coEvery { templateDao.deleteTemplateExercises(any()) } throws exception

        // Act
        val result = repository.updateTemplate(template)

        // Assert
        assertTrue(result.isFailure)
    }

    // ============ deleteTemplate Tests ============

    @Test
    fun `deleteTemplate successfully soft deletes template`() = runTest {
        // Arrange
        val templateId = "template123"
        coEvery { templateDao.deleteTemplate(any(), any(), any()) } returns Unit
        coEvery { templateDao.deleteTemplateExercises(any()) } returns Unit

        // Act
        val result = repository.deleteTemplate(templateId)

        // Assert
        assertTrue(result.isSuccess)
        coVerify { templateDao.deleteTemplate(templateId, any(), any()) }
        coVerify { templateDao.deleteTemplateExercises(templateId) }
    }

    @Test
    fun `deleteTemplate propagates exception on failure`() = runTest {
        // Arrange
        val templateId = "template123"
        val exception = RuntimeException("Delete failed")
        coEvery { templateDao.deleteTemplate(any(), any(), any()) } throws exception

        // Act
        val result = repository.deleteTemplate(templateId)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    // ============ Helper Methods ============

    private fun createMockTemplateDb(
        id: String,
        name: String,
        week1Days: Set<Int>? = null,
        week2Days: Set<Int>? = null
    ) = WorkoutTemplateDb(
        id = id,
        name = name,
        iconId = "icon_$id",
        week1Days = week1Days?.let { serializeSet(it) },
        week2Days = week2Days?.let { serializeSet(it) },
        updatedAt = LocalDateTime.now(),
        deletedAt = null
    )

    private fun createMockExerciseDb(id: String) = ExerciseDb(
        id = id,
        name = "Exercise $id",
        muscleGroup = MuscleGroup.CHEST,
        type = ExerciseType.STRENGTH,
        iconUrl = null,
        iconColor = null,
        backgroundImageUrl = null,
        backgroundColor = null,
        isCustom = false
    )

    private fun createMockWorkoutTemplate(id: String) = WorkoutTemplate(
        id = id,
        name = "Test Template",
        iconId = null,
        exerciseIds = listOf("ex1"),
        schedule = null
    )

    private fun serializeSet(set: Set<Int>): String {
        val json = kotlinx.serialization.json.Json { prettyPrint = false }
        return json.encodeToString(set)
    }
}
