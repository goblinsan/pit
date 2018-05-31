import React from "react";
import {Button, Panel} from "react-bootstrap";

class StartGameButton extends React.Component {
    render() {
        if (this.props.gameStarted) {
            return null;
        } else {
            return (
                <Button bsSize="large" bsStyle="success" onClick={this.props.startGame}>Start Game</Button>
            );
        }
    }
}

class ControlPanel extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            gameStarted: false
        };
    }


    render() {
        return (
            <Panel>
                <Panel.Body>
                    <Panel>
                        <Panel.Heading>Game States</Panel.Heading>
                        <Panel.Body>
                            <StartGameButton
                                startGame={this.props.startGame}
                            />
                        </Panel.Body>
                    </Panel>
                </Panel.Body>
            </Panel>
        );
    }
}

export default ControlPanel;