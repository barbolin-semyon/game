package com.mygdx.game

import com.badlogic.gdx.Game

/**
 * Мозги игроого процесса
 * @param initLevel начальный уровень
 * @param onSaveLevel callback функция, вызываемая при сохранения уровня игры
 */
class MyGame(
    val initLevel: Int,
    val onSaveLevel: (Int) -> Unit
) : Game() {

    /**
     * Создание переменных с экранами
     */
    private lateinit var gameOverScreen: GameOverScreen
    private lateinit var gameScreen: GameScreen
    private lateinit var menuScreen: MenuScreen
    private lateinit var gameWinRoundScreen: GameWinRoundScreen
    private lateinit var gameWinScreen: GameWinScreen
    private lateinit var levelsScreen: LevelsScreen
    val viewModel = GameViewModel()
    override fun create() {

        /**
         * Инициализация каждого экрана. Все экраны получают в качестве аргумента
         * callback функцию (функция обратного вызова), позволяющая вернутся назад в коде
         * и выполнить определенные действия - открыть другой экран.
         * При исользовании setScreen экраны переключаются между собой, предыдущий экран
         * вызывает метод onHide
         */
        viewModel.currentLevel = initLevel
        viewModel.maxLevel = initLevel

        viewModel.setParamsForLevel()

        gameOverScreen = GameOverScreen(
            onRestart =  {
                setScreen(gameScreen)
            },
            onMenu = {
                getScreen().pause()
                setScreen(menuScreen)
            }
        )
        gameScreen = GameScreen(
            viewModel = viewModel,
            onGameOver = { setScreen(gameOverScreen) },
            onWin = {
                if (viewModel.currentLevel == 5) {
                    setScreen(gameWinScreen)
                } else {
                    viewModel.currentLevel++
                    if (viewModel.currentLevel > viewModel.maxLevel) {
                        viewModel.maxLevel++
                        onSaveLevel(viewModel.maxLevel)
                    }
                    viewModel.setParamsForLevel()
                    setScreen(gameWinRoundScreen)
                }
            }
        )
        menuScreen = MenuScreen(
            onStart = { setScreen((gameScreen)) },
            onLevel = { setScreen(levelsScreen) }
        )

        gameWinRoundScreen = GameWinRoundScreen(
            onNext = {
                setScreen(gameScreen)
                     },
            onMenu = {  setScreen(menuScreen)}
        )

        gameWinScreen = GameWinScreen(
            onRestart = {
                viewModel.currentLevel = 1
                viewModel.setParamsForLevel()
            },
            onMenu = {
                setScreen(menuScreen)
            }
        )

        levelsScreen = LevelsScreen(viewModel) {
            viewModel.currentLevel = it
            viewModel.setParamsForLevel()
            setScreen(gameScreen)
        }

        setScreen(menuScreen)
    }
}

