import React from "react";
import {Button, Col, Panel, Row} from "react-bootstrap";

class ScheduleButton extends React.Component {
    constructor(props) {
        super(props);
        this.schedule = this.schedule.bind(this);
    }

    schedule() {
        fetch("http://localhost:8080/admin/schedule/" + this.props.gameState, {
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw response
                }
                return response;  //we only get here if there is no error
            })
            .then(() => {
                this.props.updateSchedule();
            })
            .catch(err => {
                err.text().then(errorMessage => {
                    alert(errorMessage);
                })
            });
    }

    render() {
        if (this.props.gameStarted) {
            return null;
        } else {
            return (
                <Button bsSize="large" bsStyle="success" onClick={this.schedule}>{this.props.label}</Button>
            );
        }
    }
}

class ControlPanel extends React.Component {
    render() {
        return (
            <Row className="show-grid">
                <Col md={12}>
                    <Panel>
                        <Panel.Body>
                            <Row className="show-grid">
                                <Col md={6}>
                                    <Panel>
                                        <Panel.Heading>Game States</Panel.Heading>
                                        <Panel.Body>
                                            <Row className="show-grid">
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='start'
                                                        label='Start Game'
                                                    />
                                                </Col>
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='open'
                                                        label='Market Open'
                                                    />
                                                </Col>
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='close'
                                                        label='Market Close'
                                                    />
                                                </Col>
                                            </Row>
                                        </Panel.Body>
                                    </Panel>
                                </Col>
                                <Col md={6}>
                                    <Panel>
                                        <Panel.Heading>Game States</Panel.Heading>
                                        <Panel.Body>
                                            <Row className="show-grid">
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='start'
                                                        label='Start Game'
                                                    />
                                                </Col>
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='open'
                                                        label='Market Open'
                                                    />
                                                </Col>
                                                <Col md={4}>
                                                    <ScheduleButton
                                                        updateSchedule={this.props.updateSchedule}
                                                        gameState='close'
                                                        label='Market Close'
                                                    />
                                                </Col>
                                            </Row>
                                        </Panel.Body>
                                    </Panel>
                                </Col>
                            </Row>
                        </Panel.Body>
                    </Panel>
                </Col>
            </Row>

        );
    }
}

export default ControlPanel;