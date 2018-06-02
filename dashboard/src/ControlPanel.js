import React from "react";
import {Button, Col, ListGroup, ListGroupItem, Panel, Row} from "react-bootstrap";

class ScheduleButton extends React.Component {
    constructor(props) {
        super(props);
        this.schedule = this.schedule.bind(this);
    }

    schedule() {
        fetch("/admin/schedule/" + this.props.gameState, {
            credentials: 'include'
        })
            .then(response => {
                if (!response.ok) {
                    throw response
                }
                return response;
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
                        <Panel.Heading>Game States</Panel.Heading>
                        <Panel.Body>
                            <ListGroup>
                                <ListGroupItem>
                                    <ScheduleButton
                                        updateSchedule={this.props.updateSchedule}
                                        gameState='start'
                                        label='Start Game'
                                    />
                                </ListGroupItem>
                                <ListGroupItem>
                                    <ScheduleButton
                                        updateSchedule={this.props.updateSchedule}
                                        gameState='open'
                                        label='Market Open'
                                    />
                                </ListGroupItem>
                                <ListGroupItem>
                                    <ScheduleButton
                                        updateSchedule={this.props.updateSchedule}
                                        gameState='close'
                                        label='Market Close'
                                    />
                                </ListGroupItem>
                            </ListGroup>
                        </Panel.Body>
                    </Panel>
                </Col>
            </Row>

        );
    }
}

export default ControlPanel;